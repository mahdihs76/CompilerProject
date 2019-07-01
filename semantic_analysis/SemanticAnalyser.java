package semantic_analysis;

import parser.Parser;
import tokenizer.Token;
import semantic_analysis.ProcTable.*;

import java.util.*;

public class SemanticAnalyser {

    public static final int KEY_ARRAY = 10001;
    public int argNum;
    private Stack<ActivationRecord> ARStack;
    private ActivationRecord currARPtr;

    private ProcTable procTable;
    private SymbolTable symbolTable;

    private Stack<Integer> semanticStack;

    private ArrayList<Instruction> codeMemory;
    private int codeMemPtr;

    private int dataMemPtr;
    private SymbolTable tempSymbolTable;
    private int tempMemPtr;

    private StringBuilder semanticErrors;
    private ArrayList<Token> input;
    private Parser.IntIndex inputIndex;


    public SemanticAnalyser(ArrayList<Token> input, Parser.IntIndex inputIndex) {
        this.ARStack = new Stack<>();
        ActivationRecord global = new ActivationRecord("Global");
        this.ARStack.add(global);
        this.currARPtr = global;

        this.procTable = new ProcTable();
        this.symbolTable = new SymbolTable();

        this.semanticStack = new Stack<>();

        this.codeMemory = new ArrayList<>();
        this.codeMemPtr = 0;

        this.dataMemPtr = 500;
        this.tempSymbolTable = new SymbolTable();
        this.tempMemPtr = 1000;

        this.input = input;
        this.inputIndex = inputIndex;
        this.semanticErrors = new StringBuilder();
    }


    /**
     * errors
     **/
    private void addSemanticError(int line, String message) {
        System.out.println("line " + line + ": " + message + '\n');
        this.semanticErrors.append("line " + line + ": " + message + '\n');
    }


    /**
     * input
     **/
    private Token getNextToken() {
        return input.get(inputIndex.value);
    }

    /** codes
     WITHBRCK := 10001
     WITHOUTBRCK := 10000
     **/


    private boolean findVarSymbol(ActivationRecord ar, int address) {
        if (ar == null) return false;
        ActivationRecord.LocalVariable lv = ar.searchLocalVariable(address);
        if (lv == null) {
            return findVarSymbol(ar.getControlLink(), address);
        } else {
            return true;
        }
    }

    private boolean findProcSymbol(ActivationRecord ar, String name) {
        if (ar == null) return false;
        boolean doesExistLP = ar.searchLocalProc(name);
        if (!doesExistLP) {
            return findProcSymbol(ar.getControlLink(), name);
        } else {
            return true;
        }
    }


    /**
     * Semantic Stack Methods
     **/
    private void pushIntoSS(int item) {
        this.semanticStack.push(item);
    }

    private int getItemFromSS(int index) {
        return this.semanticStack.get(semanticStack.size() - 1 - index);
    }

    private void popNItemsFromSS(int num) {
        for (int i = 0; i < num; i++) {
            this.semanticStack.pop();
        }
    }


    /**
     * Prosedure Stack & AR Methods
     **/
    private void pushIntoARStack(ActivationRecord ar) {
        this.ARStack.push(ar);
    }


    /**
     * Semantic Routines
     **/
    private void routine_TSINT() {
        VarType varType = VarType.INT;
        pushIntoSS(varType.ordinal());
    }

    private void routine_TSVOID() {
        VarType varType = VarType.VOID;
        pushIntoSS(varType.ordinal());
    }

    private void routine_PID() {
        String idName = getNextToken().getText();
        int address = this.dataMemPtr;
        this.symbolTable.addSymbol(idName, address);
        pushIntoSS(address);
    }

    private void routine_VARDEC() {
        int address = getItemFromSS(0);
        VarType varType = VarType.valueOf(getItemFromSS(1));
        if (varType == VarType.INT) {
            this.dataMemPtr += 4;
        }
        else if (varType == VarType.VOID) {
            addSemanticError(getNextToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");
        }

        String symbolName = symbolTable.getLastSymbol().getName();
        symbolTable.removeLastSymbol();
        symbolTable.addSymbol(symbolName, address, varType);

        this.currARPtr.addLocalVar(varType, address);

        popNItemsFromSS(2);
    }

    private void routine_ARRDEC() {
        int address = getItemFromSS(0);
        VarType varType = VarType.valueOf(getItemFromSS(1));
        int size = Integer.parseInt(getNextToken().getText());
        if (varType == VarType.INT) {
            this.dataMemPtr += 4 * size;
        }
        else if (varType == VarType.VOID) {
            addSemanticError(getNextToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");
        }

        String symbolName = symbolTable.getLastSymbol().getName();
        symbolTable.removeLastSymbol();
        VarType newVarType = VarType.INTARRAY;
        newVarType.setSize(size);
        symbolTable.addSymbol(symbolName, address, newVarType);

        this.currARPtr.addLocalVar(newVarType, address);

        popNItemsFromSS(2);
    }

    private void routine_FUNDEC() {
        String procName = this.symbolTable.getLastSymbol().getName();
        this.symbolTable.removeLastSymbol();
        VarType returnType = VarType.valueOf(getItemFromSS(1));
        popNItemsFromSS(2);


        ActivationRecord newAR = new ActivationRecord(procName);
        newAR.setControlLink(currARPtr);
        this.currARPtr = newAR;


        procTable.addProc(procName);

        int procIndex = procTable.getSize() - 1;

        Procedure proc = procTable.getProcByIndex(procIndex);
        proc.setReturnType(returnType);

        pushIntoSS(procIndex);

    }

    private void routine_WITHBRCK() {
        pushIntoSS(10001);
    }

    private void routine_WITHOUTBRCK() {
        pushIntoSS(10000);
    }

    private void routine_VOIDPARERR() {
        popNItemsFromSS(2);
        addSemanticError(getNextToken().getLine(), "Illegal type of void");
    }

    private void routine_SINGLEVOIDPAR() {
        popNItemsFromSS(2);
    }

    private void routine_PARID() {
        String parName = getNextToken().getText();
        tempSymbolTable.addSymbol(parName, 0);
    }

    private void routine_SETPAR() {
        VarType parType = VarType.valueOf(getItemFromSS(1));
        boolean isArray = (getItemFromSS(0) == KEY_ARRAY);
        popNItemsFromSS(2);

        if (parType == VarType.VOID) {
            addSemanticError(getNextToken().getLine(), "Illegal type of void");
        } else {
            if (isArray) parType = VarType.INTARRAY;

            String parName = tempSymbolTable.getLastSymbol().getName();
            tempSymbolTable.removeLastSymbol();

            Procedure proc = procTable.getProcByIndex(getItemFromSS(0));
            proc.addParam(parName, parType);
        }

    }

    private void routine_FUNENDPARS() {
        Procedure proc = procTable.getProcByIndex(getItemFromSS(0));
        proc.setCodeStartAdress(codeMemPtr);
    }

    private void routine_FUNJPCALLER() {
        Procedure proc = procTable.getProcByIndex(getItemFromSS(0));
        currARPtr = currARPtr.getControlLink();
        proc.setCodeEndAdress(codeMemPtr);
        popNItemsFromSS(1);
    }

    private void routine_IFSAVE() {
        pushIntoSS(codeMemPtr);
        codeMemPtr++;
    }

    private void routine_IFJPSAVE() {
        int index = getItemFromSS(0);
        codeMemory.add(index, new Instruction(Instruction.Type.JPF, new int[]{getItemFromSS(1), codeMemPtr}));
        pushIntoSS(codeMemPtr);
        codeMemPtr++;
        popNItemsFromSS(2);
    }

    private void routine_ENDIF() {
        int index = getItemFromSS(0);
        codeMemory.add(index, new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));
        popNItemsFromSS(1);
    }

    private void routine_WHLABEL() {
        pushIntoSS(codeMemPtr);
    }

    private void routine_WHSAVE() {
        pushIntoSS(codeMemPtr);
        codeMemPtr++;
    }

    private void routine_ENDWH() {
        int index = getItemFromSS(0);
        codeMemory.add(index, new Instruction(Instruction.Type.JPF, new int[]{getItemFromSS(1), codeMemPtr + 1}));
        codeMemory.add(codeMemPtr, new Instruction(Instruction.Type.JP, new int[]{getItemFromSS(2)}));
        codeMemPtr++;
        popNItemsFromSS(3);
    }

    private void routine_SYMBOL() {
        String idName = getNextToken().getText();
        int address = symbolTable.getAddressByName(idName);
        if (address == -1 && findProcSymbol(currARPtr, idName)){
            int procIndex = procTable.getProcIndexByName(idName);
            pushIntoSS(procIndex);
            pushIntoSS(address);
        } else if (address != -1 && findVarSymbol(currARPtr, address)) {
            pushIntoSS(address);
        } else {
            addSemanticError(getNextToken().getLine(), idName + " is not defined");
        }
    }

    private void routine_GETARR() {
        int idAddress = getItemFromSS(1);
        if(idAddress == -1) {
            Procedure proc = procTable.getProcByIndex(getItemFromSS(2));
            addSemanticError(getNextToken().getLine(), proc.getName() + " is not defined");
            popNItemsFromSS(3);
            return;
        }

        int offset = getItemFromSS(0);
        popNItemsFromSS(2);
        pushIntoSS (idAddress + offset * 4);
    }

    private void routine_FUNCALL() {
        int idAddress = getItemFromSS(0);
        String procName = symbolTable.getNameByAddress(idAddress);
        if(idAddress != -1) {
            addSemanticError(getNextToken().getLine(), procName + " is not defined");
        }
        popNItemsFromSS(1);

    }

    private void routine_FUNCALLJP() {
        int procIndex = getItemFromSS(argNum);
        Procedure procedure = procTable.getProcByIndex(procIndex);
        if (procedure.getParamsCount() != argNum) {
            addSemanticError(getNextToken().getLine(), "Mismatch in number of arguments in " + procedure.getName());
            popNItemsFromSS(argNum + 1);
            return;
        }
        for (int i = argNum - 1; i >= 0; i--) {
            int argAddress = getItemFromSS(i);
            SymbolTable.Symbol ithArgumentSymbol = symbolTable.getByAddress(argAddress);
            if (ithArgumentSymbol.varType == VarType.INTARRAY) {
                symbolTable.addSymbol(procedure.getParamNameByIndex(argNum - 1 - i), argAddress, ithArgumentSymbol.varType);
                ActivationRecord activationRecord = currARPtr.findAccessibleChild(procedure.getName());
                activationRecord.addLocalVar(ithArgumentSymbol.varType, dataMemPtr);
                dataMemPtr += 4 * procedure.getParamTypeByIndex(argNum - 1 - i).getSize();
            } else if(ithArgumentSymbol.varType == VarType.INT) {
                symbolTable.addSymbol(procedure.getParamNameByIndex(argNum - 1 - i), dataMemPtr, ithArgumentSymbol.varType);
                codeMemory.add(new Instruction(Instruction.Type.ASSIGN, new int[]{argAddress, dataMemPtr}));
                codeMemPtr += 1;
                ActivationRecord activationRecord = currARPtr.findAccessibleChild(procedure.getName());
                activationRecord.addLocalVar(ithArgumentSymbol.varType, dataMemPtr);
                dataMemPtr += 4;
            } else {
                addSemanticError(getNextToken().getLine(), "incompatible type in function argument in " + procedure.getName());
            }
        }

        popNItemsFromSS(argNum);
        codeMemory.add(new Instruction(Instruction.Type.JP, new int[]{procedure.getCodeStartAdress()}));
        codeMemPtr++;
        codeMemory.add(procedure.getCodeEndAdress(), new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));

        popNItemsFromSS(1);

    }

    private void routine_NUM() {
        int immediateNum = Integer.parseInt(getNextToken().getText());
        tempSymbolTable.addSymbol("", tempMemPtr, VarType.INT);
        codeMemory.add(new Instruction(Instruction.Type.ASSIGN, new int[]{immediateNum, tempMemPtr}, new boolean[]{true, false}));
        pushIntoSS(tempMemPtr);

        codeMemPtr++;
        tempMemPtr += 4;
    }

    private void routine_FMINUS() {
        int address = getItemFromSS(0);
        popNItemsFromSS(1);

        codeMemory.add(new Instruction(Instruction.Type.MULT, new int[]{-1, }, new boolean[]{true, false, false}));

    }

    private void routine_FPLUS() {

    }







    public void executeSemanticRoutine(RoutineType routineType) {
        switch (routineType) {
            case TSINT:
                routine_TSINT();
                break;
            case TSVOID:
                routine_TSVOID();
                break;
            case PID:
                routine_PID();
                break;
            case VARDEC:
                routine_VARDEC();
                break;
            case ARRDEC:
                routine_ARRDEC();
                break;
            case FUNDEC:
                routine_FUNDEC();
                break;
            case WITHBRCK:
                routine_WITHBRCK();
                break;
            case WITHOUTBRCK:
                routine_WITHOUTBRCK();
                break;
            case VOIDPARERR:
                routine_VOIDPARERR();
                break;
            case SINGLEVOIDPAR:
                routine_SINGLEVOIDPAR();
                break;
            case PARID:
                routine_PARID();
                break;
            case SETPAR:
                routine_SETPAR();
                break;
            case FUNENDPARS:
                routine_FUNENDPARS();
                break;
            case FUNJPCALLER:
                routine_FUNJPCALLER();
                break;
            case IF_SAVE:
                routine_IFSAVE();
                break;
            case IF_JP_SAVE:
                routine_IFJPSAVE();
                break;
            case END_IF:
                routine_ENDIF();
                break;
            case WH_LABEL:
                routine_WHLABEL();
                break;
            case WH_SAVE:
                routine_WHSAVE();
                break;
            case ENDWH:
                routine_ENDWH();
                break;
            case GETARR:
                routine_GETARR();
                break;
            case FUNCALL:
                routine_FUNCALL();
                break;
            case SYMBOL:
                routine_SYMBOL();
                break;
            case FUNCALLJP:
                routine_FUNCALLJP();
                break;
            case NUM:
                routine_NUM();
                break;
            case FMINUS:
                routine_FMINUS();
                break;
            case FPLUS:
                routine_FPLUS();
                break;


        }

    }


    public enum RoutineType {
        TSINT,
        TSVOID,
        PID,
        VARDEC,
        ARRDEC,
        FUNDEC,
        WITHBRCK,
        WITHOUTBRCK,
        VOIDPARERR,
        SINGLEVOIDPAR,
        PARID,
        SETPAR,
        FUNENDPARS,
        FUNJPCALLER,
        IF_SAVE,
        IF_JP_SAVE,
        END_IF,
        WH_LABEL,
        WH_SAVE,
        ENDWH,
        GETARR,
        FUNCALL,
        SYMBOL,
        FUNCALLJP,
        NUM,
        FPLUS,
        FMINUS,

    }


}

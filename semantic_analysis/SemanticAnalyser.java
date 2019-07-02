package semantic_analysis;

import parser.Parser;
import tokenizer.Token;
import semantic_analysis.ProcTable.*;
import utility.HashUtils;

import java.util.*;

public class SemanticAnalyser {

    private static final int KEY_ARRAY = 10001;
    private static final int KEY_PLUS = 10005;
    private static final int KEY_MINUS = 10006;
    private static final int KEY_LT = 10007;
    private static final int KEY_EQ = 10008;

    private int argNum;
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
        procTable.addProc("output");
        Procedure outputProcedure = procTable.getProcByName("output");
        outputProcedure.setReturnType(VarType.VOID);

        this.symbolTable = new SymbolTable();

        this.semanticStack = new Stack<>();

        this.codeMemory = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            codeMemory.add(null);
        }
        this.codeMemPtr = 0;

        this.dataMemPtr = 500;
        this.tempSymbolTable = new SymbolTable();
        this.tempMemPtr = 1000;

        this.input = input;
        this.inputIndex = inputIndex;
        this.semanticErrors = new StringBuilder();
    }



    private void addInstructionToCodeMemory(int index, Instruction instr) {
        codeMemory.set(index, instr);
    }
    private void addInstructionToCodeMemory(Instruction instr) {
        codeMemory.set(codeMemPtr++, instr);
    }




    /**
     * errors & result
     **/
    private void addSemanticError(int line, String message) {
        System.out.println("line " + line + ": " + message + '\n');
        this.semanticErrors.append("line ").append(line).append(": ").append(message).append('\n');
    }

    public String getSemanticErrorsString() {
        return semanticErrors.toString();
    }

    public String getIntermediateCodeString() {
        StringBuilder sb = new StringBuilder();
        int line = 0;
        for (Instruction instruction: codeMemory)
            if(instruction != null)
                sb.append(line++).append("  ").append(instruction);
            else
                sb.append(line++).append("\n");
        return sb.toString();
    }




    /**
     * input
     **/
    private Token getLastToken() {
        return input.get(inputIndex.value);
    }

    private Token getOneToLastToken() {
        return input.get(inputIndex.value - 1);
    }

    public ArrayList<Instruction> getCodeMemory() {
        return codeMemory;
    }

    /**
     * codes
     * WITHBRCK := 10001
     * WITHOUTBRCK := 10000
     **/



    private boolean findVarSymbol(ActivationRecord ar, int address, String idName) {
        if (ar == null) return false;
        ActivationRecord.LocalVariable lv = ar.searchLocalVariable(address, idName);
        boolean findVarInParams = procTable.getProcByName(ar.getName()).findParam(idName);
        if (lv == null && !findVarInParams) {
            return findVarSymbol(ar.getControlLink(), address, idName);
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

    public ProcTable getProcTable() {
        return procTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
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
        String idName = getLastToken().getText();
        int address = this.dataMemPtr;
        this.symbolTable.addSymbol(idName, address);
        pushIntoSS(address);
    }

    private void routine_VARDEC() {
        int address = getItemFromSS(0);
        VarType varType = VarType.valueOf(getItemFromSS(1));
        if (varType == VarType.INT) {
            this.dataMemPtr += 4;
        } else if (varType == VarType.VOID) {
            addSemanticError(getLastToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");
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
        int size = Integer.parseInt(getLastToken().getText());
        if (varType == VarType.INT) {
            this.dataMemPtr += 4 * size;
        } else if (varType == VarType.VOID) {
            addSemanticError(getLastToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");
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
        if(currARPtr.getName().equals("Global") && !newAR.getName().equals("main"))
            addSemanticError(getLastToken().getLine(), "unacceptable name for global function (" + newAR.getName() + ")");
        newAR.setControlLink(currARPtr);
        currARPtr.addLocalProcedure(procName);
        currARPtr = newAR;


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
        addSemanticError(getLastToken().getLine(), "Illegal type of void");
    }

    private void routine_SINGLEVOIDPAR() {
        popNItemsFromSS(1);
    }

    private void routine_PARID() {
        String parName = getLastToken().getText();
        symbolTable.addSymbol(parName, HashUtils.makeHash(parName));
    }

    private void routine_SETPAR() {
        VarType parType = VarType.valueOf(getItemFromSS(1));
        boolean isArray = (getItemFromSS(0) == KEY_ARRAY);
        popNItemsFromSS(2);

        if (parType == VarType.VOID) {
            addSemanticError(getLastToken().getLine(), "Illegal type of void");
        } else {
            if (isArray) parType = VarType.INTARRAY;

            String parName = symbolTable.getLastSymbol().getName();

            Procedure proc = procTable.getProcByIndex(getItemFromSS(0));
            proc.addParam(parName, parType);
        }

    }

    private void routine_FUNENDPARS() {
        Procedure proc = procTable.getProcByIndex(getItemFromSS(0));

        proc.setBeforeCodeStartAddress(codeMemPtr);
        codeMemPtr++;

        proc.setCodeStartAddress(codeMemPtr);
    }

    private void routine_FUNJPCALLER() {
        Procedure proc = procTable.getProcByIndex(getItemFromSS(0));

        proc.setBeforeCodeEndAddress(codeMemPtr);

        int returnLineMemAddress = proc.getReturnLineMemAddress();
        if(returnLineMemAddress != -1)
            addInstructionToCodeMemory(returnLineMemAddress, new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));
        else {
            if(!proc.getName().equals("output"))
                addSemanticError(getOneToLastToken().getLine(), "There should be a return statement in " + proc.getName() + " function");
        }

        proc.setCodeEndAdress(codeMemPtr);
        codeMemPtr++;
        if(!proc.getName().equals("main")) {
            addInstructionToCodeMemory(proc.getBeforeCodeStartAddress(), new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));
        }

        currARPtr = currARPtr.getControlLink();

        popNItemsFromSS(1);
    }

    private void routine_IFSAVE() {
        pushIntoSS(codeMemPtr);
        codeMemPtr++;
    }

    private void routine_IFJPSAVE() {
        int index = getItemFromSS(0);
        addInstructionToCodeMemory(index, new Instruction(Instruction.Type.JPF, new int[]{getItemFromSS(1), codeMemPtr}));
        pushIntoSS(codeMemPtr);
        codeMemPtr++;
        popNItemsFromSS(2);
    }

    private void routine_ENDIF() {
        int index = getItemFromSS(0);
        addInstructionToCodeMemory(index, new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));
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
        addInstructionToCodeMemory(index, new Instruction(Instruction.Type.JPF, new int[]{getItemFromSS(1), codeMemPtr + 1}));
        addInstructionToCodeMemory(new Instruction(Instruction.Type.JP, new int[]{getItemFromSS(2)}));
        popNItemsFromSS(3);
    }

    private void routine_BREAK() {
        addInstructionToCodeMemory(new Instruction(Instruction.Type.JP, new int[]{getItemFromSS(2)}));

    }

    private void routine_CONTINUE() {
        addInstructionToCodeMemory(new Instruction(Instruction.Type.JP, new int[]{getItemFromSS(2)}));
        popNItemsFromSS(3);
    }


    private void routine_SYMBOL() {
        String idName = getLastToken().getText();
        int address = symbolTable.getAddressByName(idName);
        if (address == -1 && findProcSymbol(currARPtr, idName)) {
            int procIndex = procTable.getProcIndexByName(idName);
            pushIntoSS(procIndex);
            pushIntoSS(address);
        } else if (address != -1 && findVarSymbol(currARPtr, address, idName)) {
            pushIntoSS(address);
        } else {
            if(!idName.equals("output"))
                addSemanticError(getLastToken().getLine(), idName + " is not defined");
            else {
                pushIntoSS(procTable.getProcIndexByName("output"));
                pushIntoSS(address);
            }

        }
    }

    private void routine_GETARR() {
        int idAddress = getItemFromSS(1);
        if (idAddress == -1) {
            Procedure proc = procTable.getProcByIndex(getItemFromSS(2));
            addSemanticError(getLastToken().getLine(), proc.getName() + " is not defined");
            popNItemsFromSS(3);
            return;
        }

        int offset = getItemFromSS(0);
        popNItemsFromSS(2);

        addInstructionToCodeMemory(new Instruction(Instruction.Type.MULT, new int[]{offset, 4, tempMemPtr}, new boolean[]{false, true, false}));
        symbolTable.addSymbol(tempMemPtr, VarType.INT);
        addInstructionToCodeMemory(new Instruction(Instruction.Type.ADD, new int[]{tempMemPtr, idAddress, tempMemPtr}));

        pushIntoSS(tempMemPtr);
        tempMemPtr += 4;
    }

    private void routine_FUNCALL() {
        int idAddress = getItemFromSS(0);

        String procName = getOneToLastToken().getText();

        if (!procName.equals("output") && idAddress != -1) {
            addSemanticError(getLastToken().getLine(), procName + " is not defined");
        }

        popNItemsFromSS(1);

    }

    private void routine_FUNCALLJP() {
        int procIndex = getItemFromSS(argNum);
        Procedure procedure = procTable.getProcByIndex(procIndex);

        if(procedure.getName().equals("output")){
            if (argNum != 1) {
                addSemanticError(getLastToken().getLine(), "Mismatch in number of arguments in " + procedure.getName());
                popNItemsFromSS(2);
                return;
            }
            int argAddress = getItemFromSS(0);
            SymbolTable.Symbol ithArgumentSymbol = symbolTable.getByAddress(argAddress);
            if (ithArgumentSymbol.getVarType() == VarType.INTARRAY) {
                addSemanticError(getOneToLastToken().getLine(), "unacceptable input type for function 'output'");
            } else if (ithArgumentSymbol.getVarType() == VarType.INT) {
                addInstructionToCodeMemory(new Instruction(Instruction.Type.PRINT, new int[]{argAddress}));
            } else {
                addSemanticError(getLastToken().getLine(), "incompatible input type for function 'output");
            }
            popNItemsFromSS(2);

            return;
        }

        ActivationRecord activationRecord = currARPtr.findAccessibleChild(procedure.getName());

        if (procedure.getParamsCount() != argNum) {
            addSemanticError(getLastToken().getLine(), "Mismatch in number of arguments in " + procedure.getName());
            popNItemsFromSS(argNum + 1);
            return;
        }
        for (int i = argNum - 1; i >= 0; i--) {
            int argAddress = getItemFromSS(i);
            SymbolTable.Symbol ithArgumentSymbol = symbolTable.getByAddress(argAddress);
            if (ithArgumentSymbol.getVarType() == VarType.INTARRAY) {
                symbolTable.addSymbol(procedure.getParamNameByIndex(argNum - 1 - i), argAddress, ithArgumentSymbol.getVarType());
                activationRecord.addLocalVar(ithArgumentSymbol.getVarType(), dataMemPtr);
                dataMemPtr += 4 * procedure.getParamTypeByIndex(argNum - 1 - i).getSize();
            } else if (ithArgumentSymbol.getVarType() == VarType.INT) {
                symbolTable.addSymbol(procedure.getParamNameByIndex(argNum - 1 - i), dataMemPtr, ithArgumentSymbol.getVarType());
                addInstructionToCodeMemory(new Instruction(Instruction.Type.ASSIGN, new int[]{argAddress, dataMemPtr}));
                activationRecord.addLocalVar(ithArgumentSymbol.getVarType(), dataMemPtr);
                dataMemPtr += 4;
            } else {
                addSemanticError(getLastToken().getLine(), "incompatible type in function argument in " + procedure.getName());
            }
        }

        popNItemsFromSS(argNum);
        addInstructionToCodeMemory(new Instruction(Instruction.Type.JP, new int[]{procedure.getCodeStartAddress()}));
        addInstructionToCodeMemory(procedure.getBeforeCodeEndAddress(), new Instruction(Instruction.Type.JP, new int[]{codeMemPtr}));

        int returnAddress = activationRecord.getReturnAddress();

        popNItemsFromSS(1);

        if (returnAddress != -1) {
            pushIntoSS(returnAddress);
        }


    }

    private void routine_ZEROARGNUM() {
        argNum = 0;
    }

    private void routine_COUNTARG() {
        argNum++;
    }

    private void routine_NUM() {
        int immediateNum = Integer.parseInt(getLastToken().getText());
        symbolTable.addSymbol(dataMemPtr, VarType.INT);
        addInstructionToCodeMemory(new Instruction(Instruction.Type.ASSIGN, new int[]{immediateNum, dataMemPtr}, new boolean[]{true, false}));
        pushIntoSS(dataMemPtr);

        dataMemPtr += 4;
    }

    private void routine_FMINUS() {
        int address = getItemFromSS(0);
        popNItemsFromSS(1);

        int resultAddress = dataMemPtr;
        dataMemPtr += 4;
        symbolTable.addSymbol(resultAddress, VarType.INT);

        pushIntoSS(resultAddress);
        addInstructionToCodeMemory(new Instruction(Instruction.Type.MULT, new int[]{-1, address, resultAddress}, new boolean[]{true, false, false}));
    }

    private void routine_FMULT() {
        threeOperandsRoutineCall(getItemFromSS(1), getItemFromSS(0), Instruction.Type.MULT);
    }

    private void routine_PLUS() {
        pushIntoSS(KEY_PLUS);
    }

    private void routine_MINUS() {
        pushIntoSS(KEY_MINUS);

    }

    private void routine_PLORMI() {
        switch (getItemFromSS(1)){
            case KEY_PLUS:
                threeOperandsRoutineCall(getItemFromSS(2), getItemFromSS(0), Instruction.Type.ADD);
                break;
            case KEY_MINUS:
                threeOperandsRoutineCall(getItemFromSS(2), getItemFromSS(0), Instruction.Type.SUB);
                break;
        }
    }

    private void routine_LT() {
        pushIntoSS(KEY_LT);
    }

    private void routine_EQ() {
        pushIntoSS(KEY_EQ);
    }

    private void routine_LTOREQ() {
        switch (getItemFromSS(1)){
            case KEY_LT:
                threeOperandsRoutineCall(getItemFromSS(2), getItemFromSS(0), Instruction.Type.LT);
                break;
            case KEY_EQ:
                threeOperandsRoutineCall(getItemFromSS(2), getItemFromSS(0), Instruction.Type.EQ);
                break;
        }
    }

    private void routine_ASSIGN() {
        twoOperandsRoutineCall(getItemFromSS(0), getItemFromSS(1), Instruction.Type.ASSIGN);
//        popNItemsFromSS(1);
    }

    private void routine_VOIDRETURN() {
        String procName = currARPtr.getName();
        procTable.getProcByName(procName).setReturnLineMemAddress(codeMemPtr);

        codeMemPtr++;

//        popNItemsFromSS(1);
    }


    private void routine_RETURN() {
        currARPtr.setReturnAddress(getItemFromSS(0));
        popNItemsFromSS(1);

        String procName = currARPtr.getName();
        procTable.getProcByName(procName).setReturnLineMemAddress(codeMemPtr);

        codeMemPtr++;

        //popNItemsFromSS(1);

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
            case ZEROARGNUM:
                routine_ZEROARGNUM();
                break;
            case COUNTARG:
                routine_COUNTARG();
                break;
            case NUM:
                routine_NUM();
                break;
            case FMINUS:
                routine_FMINUS();
                break;
            case FMULT:
                routine_FMULT();
                break;
            case PLUS:
                routine_PLUS();
                break;
            case MINUS:
                routine_MINUS();
                break;
            case PLORMI:
                routine_PLORMI();
                break;
            case LT:
                routine_LT();
                break;
            case EQ:
                routine_EQ();
                break;
            case LTOREQ:
                routine_LTOREQ();
                break;
            case ASSIGN:
                routine_ASSIGN();
                break;
            case RETURN:
                routine_RETURN();
                break;
            case VOIDRETURN:
                routine_VOIDRETURN();
                break;
            case CONTINUE:
                routine_CONTINUE();
                break;
            case BREAK:
                routine_BREAK();
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
        COUNTARG,
        ZEROARGNUM,
        NUM,
        FMINUS,
        FMULT,
        PLUS,
        MINUS,
        PLORMI,
        LT,
        EQ,
        LTOREQ,
        ASSIGN,
        RETURN,
        VOIDRETURN,
        CONTINUE,
        BREAK
    }


    private SymbolTable.Symbol getAppropriateSymbol(int address){
        if (HashUtils.isHashCode(address)) {
            return symbolTable.getSymbolByHashCode(address);
        } else {
            return symbolTable.getByAddress(address);
        }
    }


    private void RoutineCall(int first, int second, Instruction.Type type, int[] operands){
        if (getAppropriateSymbol(first).getVarType() != getAppropriateSymbol(second).getVarType()) {
            addSemanticError(getLastToken().getLine(), "Type mismatch in operands.");
            popNItemsFromSS(2);
            return;
        }
        if(type == Instruction.Type.MULT)
            popNItemsFromSS(operands.length - 1);
        else
            popNItemsFromSS(operands.length);

        addInstructionToCodeMemory(new Instruction(type, operands));
        if (type != Instruction.Type.ASSIGN) {
            symbolTable.addSymbol(dataMemPtr, VarType.INT);
            pushIntoSS(dataMemPtr);
            dataMemPtr += 4;
        }
    }

    private void threeOperandsRoutineCall(int first, int second, Instruction.Type type){
        RoutineCall(first, second, type, new int[]{first, second, dataMemPtr});
    }

    private void twoOperandsRoutineCall(int first, int second, Instruction.Type type){
        RoutineCall(first, second, type, new int[]{first, second});
    }


}

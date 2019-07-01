package semantic_analysis;

import parser.Parser;
import tokenizer.Token;
import semantic_analysis.ProcTable.*;

import java.util.*;

public class SemanticAnalyser {

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
        this.ARStack = new Stack<ActivationRecord>();
        this.ARStack.add(new ActivationRecord("Global"));
        this.currARPtr = null;

        this.procTable = new ProcTable();
        this.symbolTable = new SymbolTable();

        this.semanticStack = new Stack<Integer>();

        this.codeMemory = new ArrayList<Instruction>();
        this.codeMemPtr = 0;

        this.dataMemPtr = 500;
        this.tempSymbolTable = new SymbolTable();
        this.tempMemPtr = 0;

        this.input = input;
        this.inputIndex = inputIndex;
        this.semanticErrors = new StringBuilder();
    }


    /** errors **/
    private void addSemanticError(int line, String message) {
        System.out.println("line " + line + ": " + message + '\n');
        this.semanticErrors.append("line " + line + ": " + message + '\n');
    }



    /** input **/
    private Token getNextToken() { return input.get(inputIndex.value); }

    /** codes
        WITHBRCK := 10001
        WITHOUTBRCK := 10000
    **/






    /** Semantic Stack Methods **/
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


    /** Prosedure Stack & AR Methods **/
    private void pushIntoARStack(ActivationRecord ar) { this.ARStack.push(ar); }








    /** Semantic Routines **/
    private void routine_TSINT() {
        VarType varType = VarType.INT;
        pushIntoSS(VarType.getValue(varType));
    }

    private void routine_TSVOID() {
        VarType varType = VarType.VOID;
        pushIntoSS(VarType.getValue(varType));
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
        if(varType == VarType.INT)
            this.dataMemPtr += 4;
        else if(varType == VarType.VOID)
            addSemanticError(getNextToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");

        this.currARPtr.addLocalVar(varType, address);

        popNItemsFromSS(2);
    }

    private void routine_ARRDEC() {
        int address = getItemFromSS(0);
        VarType varType = VarType.valueOf(getItemFromSS(1));
        int size = Integer.parseInt(getNextToken().getText());
        if(varType == VarType.INT)
            this.dataMemPtr += 4 * size;
        else if(varType == VarType.VOID)
            addSemanticError(getNextToken().getLine(), "‫‪Illegal‬‬ ‫‪type‬‬ ‫‪of‬‬ ‫‪void");

        this.currARPtr.addLocalVar(varType, address, size);

        popNItemsFromSS(2);
    }

    private void routine_FUNDEC() {
        String procName = this.symbolTable.getLastSymbol().getName();
        this.symbolTable.removeLastSymbol();
        VarType returnType = VarType.valueOf(getItemFromSS(1));
        popNItemsFromSS(2);

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
        VarType parType = VarType.valueOf(semanticStack.get(1));
        boolean isArray = (semanticStack.get(0) == 10001);
        popNItemsFromSS(2);

        if(parType == VarType.VOID) {
            addSemanticError(getNextToken().getLine(), "Illegal type of void");
        }
        else {
            if(isArray)  parType = VarType.INTARRAY;

            String parName = tempSymbolTable.getLastSymbol().getName();
            tempSymbolTable.removeLastSymbol();

            Procedure proc = procTable.getProcByIndex(semanticStack.get(0));
            proc.addParam(parName, parType);
        }

    }

    private void routine_FUNENDPARS() {
        Procedure proc = procTable.getProcByIndex(semanticStack.get(0));
        proc.setCodeStartAdress(codeMemPtr);
    }

    private void routine_FUNJPCALLER() {
        Procedure proc = procTable.getProcByIndex(semanticStack.get(0));
        proc.setCodeEndAdress(codeMemPtr);
        popNItemsFromSS(1);
    }












    public void executeSemanticRoutine(RoutineType routineType) {
        switch(routineType) {
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
    }




}

package semantic_analysis;

import java.util.*;

public class SemanticAnalyser {

    private Stack<ActivationRecord> procedureStack;
    private SymbolTable symbolTable;
    private Stack<Integer> semanticStack;

    private ArrayList<Instruction> codeMemory;
    private int codeMemPtr;
    private int dataMemPtr;
    private SymbolTable tempSymbolTable;
    private int tempMemPtr;


    public SemanticAnalyser() {
        this.procedureStack = new Stack<ActivationRecord>();
        this.symbolTable = new SymbolTable();
        this.semanticStack = new Stack<Integer>();
        this.codeMemory = new ArrayList<Instruction>();
        this.codeMemPtr = 0;
        this.dataMemPtr = 0;
        this.tempSymbolTable = new SymbolTable();
        this.tempMemPtr = 0;
    }



    public void doAction(ActionType actionType) {
        switch(actionType) {
            case PID:
                int a = 0;
                break;
            case SAVE:

                break;
            case LABEL:

                break;

        }

        return;
    }


    public enum ActionType {
        PID,
        SAVE,
        LABEL;
    }




}

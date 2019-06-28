package semantic_analysis;


/*
    This is the class representing "intermediate code instructions".
 */


import java.util.ArrayList;

public class Instruction {

    private Type type;
    private int[] operands;

    public Instruction(Type type) {
        this.type = type;
        this.operands = new int[type.numOperands];
    }

    public Instruction(Type type, int[] operands) {
        this.type = type;
        this.operands = operands;
    }




    public enum Type {
        ADD(3),
        SUB(3),
        AND(3),
        ASSIGN(2),
        EQ(3),
        JPF(2),
        JP(1),
        LT(3),
        MULT(3),
        NOT(2),
        PRINT(1);

        private int numOperands;

        private Type(int numOperands) {
            this.numOperands = numOperands;
        }

    }


}

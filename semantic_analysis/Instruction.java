package semantic_analysis;


/*
    This is the class representing "intermediate code instructions".
 */


import java.util.ArrayList;
import java.util.Arrays;

public class Instruction {

    private Type type;
    private int[] operands;
    private boolean[] boolArray;


    public Instruction(Type type, int[] operands) {
        boolean[] boolArray = new boolean[operands.length];
        for (int i = 0; i < operands.length; i++) {
            boolArray[i] = false;
        }
        this.type = type;
        this.operands = operands;
        this.boolArray = boolArray;
    }

    public Instruction(Type type, int[] operands, boolean[] boolArray) {
        this.type = type;
        this.operands = operands;
        this.boolArray = boolArray;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type.toString());
        for (int i = 0; i < type.numOperands; i++) {
            int operand = operands[i];
            sb.append(", ");
            if(boolArray[i])
                sb.append("#");
            sb.append(Integer.toString(operand));
        }
        sb.append(", ".repeat(Math.max(0, 3 - type.numOperands)));
        return '(' + sb.toString() + ')' + '\n';
    }
}

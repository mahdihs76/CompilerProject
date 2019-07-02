package utility;

import semantic_analysis.Instruction;
import semantic_analysis.SemanticAnalyser;
import semantic_analysis.SymbolTable;

import java.util.ArrayList;

public class HashUtils {

    public static final int BASE_HASH_OPERAND = 111111;

    public static SemanticAnalyser normalizeHashOperands(SemanticAnalyser analyser) {
        ArrayList<Instruction> codeMemory = analyser.getCodeMemory();
        for (int i = 0; i < codeMemory.size(); i++) {
            Instruction instruction = codeMemory.get(i);
            if (instruction == null) continue;
            int[] operands = instruction.getOperands();
            int[] clone = operands.clone();
            for (int i1 = 0; i1 < operands.length; i1++) {
                int operand = operands[i1];
                if (isHashCode(operand)) {
                    operand -= BASE_HASH_OPERAND;
                    SymbolTable.Symbol s = analyser.getSymbolTable().getSymbolByHashCode(operand);
                    clone[i1] = operand;
                }
            }
            instruction.setOperands(clone);
            codeMemory.set(i, instruction);
        }
        return analyser;
    }



    public static boolean isHashCode(int operand) {
        return operand > BASE_HASH_OPERAND;
    }

    public static int makeHash(String parName) {
        return BASE_HASH_OPERAND + parName.hashCode();
    }
}

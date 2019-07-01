package semantic_analysis;

public enum VarType {
    INT,
    INTARRAY,
    VOID;


    public static int getValue(VarType varType) {
        int ret = -1;
        switch (varType) {
            case INT: ret = 0;
            case INTARRAY: ret = 1;
            case VOID: ret = 2;
        }
        return ret;
    }

    public static VarType valueOf(int value) {
        VarType ret = null;
        switch (value) {
            case 0: ret = INT;
            case 1: ret = INTARRAY;
            case 2: ret = VOID;
        }
        return ret;
    }
}

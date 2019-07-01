package semantic_analysis;

public enum VarType {
    INT,
    INTARRAY,
    VOID;

    private int size = 1;

    public void setSize(int size) {
        this.size = size;
    }
    public int getSize() {
        return size;
    }


    public static VarType valueOf(int value) {
        switch (value) {
            case 0: return INT;
            case 1: return INTARRAY;
            case 2: return VOID;
            default: return null;
        }
    }
}

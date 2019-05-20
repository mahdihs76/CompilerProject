package parser.models.types;

import parser.models.Type;

public class IntType extends Type {
    public static IntType instance = new IntType();

    private IntType() {
    }

    @Override
    public String toString() {
        return "int";
    }
}

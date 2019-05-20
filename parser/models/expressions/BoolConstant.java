package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;

public class BoolConstant implements Expression {
    private final boolean value;

    public BoolConstant(boolean value) {
        this.value = value;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BoolConstant && this.value == ((BoolConstant) obj).value;
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}

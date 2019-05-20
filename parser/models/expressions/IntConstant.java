package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;

public class IntConstant implements Expression {
    private final int value;

    public IntConstant(int value) {
        this.value = value;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntConstant && this.value == ((IntConstant) obj).value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}

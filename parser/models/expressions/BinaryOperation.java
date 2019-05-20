package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;

public class BinaryOperation implements Expression {
    private final Expression left;
    private final String opName;
    private final Expression right;

    public BinaryOperation(Expression left, String opName, Expression right) {
        this.left = left;
        this.opName = opName;
        this.right = right;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryOperation) {
            BinaryOperation that = (BinaryOperation)obj;
            return
                    this.getClass().equals(that.getClass()) &&
                    this.left.equals(that.left) &&
                    this.opName.equals(that.opName) &&
                    this.right.equals(that.right);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (left.hashCode() << 16) ^ right.hashCode();
    }

    @Override
    public String toString() {
        return "(" + left + " " + opName + " " + right + ")";
    }
}

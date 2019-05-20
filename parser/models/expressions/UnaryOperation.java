package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;

public class UnaryOperation implements Expression {
    private final String opName;
    private final Expression operand;

    public UnaryOperation(String opName, Expression operand) {
        this.opName = opName;
        this.operand = operand;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryOperation) {
            UnaryOperation that = (UnaryOperation)obj;
            return
                    this.getClass().equals(that.getClass()) &&
                    this.operand.equals(that.operand);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return operand.hashCode();
    }

    @Override
    public String toString() {
        return "(" + opName + operand + ")";
    }
}

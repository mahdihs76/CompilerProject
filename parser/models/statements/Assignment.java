package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Expression;
import parser.models.Statement;

public class Assignment implements Statement {
    private final String varName;
    public final Expression expression;

    public Assignment(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Assignment) {
            Assignment that = (Assignment)obj;
            return
                    this.varName.equals(that.varName) &&
                    this.expression.equals(that.expression);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return varName.hashCode() << 16 ^ expression.hashCode();
    }

    @Override
    public String toString() {
        return varName + " := " + expression;
    }
}

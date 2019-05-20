package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Expression;
import parser.models.Statement;
import parser.models.Type;

public class Declaration implements Statement {
    private final String varName;
    private final Type type;
    public final Expression expression;

    public Declaration(String varName, Type type, Expression expression) {
        this.varName = varName;
        this.type = type;
        this.expression = expression;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Declaration) {
            Declaration that = (Declaration)obj;
            return
                    this.varName.equals(that.varName) &&
                    this.type.equals(that.type) &&
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
        return varName + " : " + type + " := " + expression;
    }
}

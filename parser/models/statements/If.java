package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Expression;
import parser.models.Statement;

public class If implements Statement {
    public final Expression condition;
    public final Statement thenClause;
    public final Statement elseClause;

    public If(Expression condition, Statement thenClause, Statement elseClause) {
        this.condition = condition;
        this.thenClause = thenClause;
        this.elseClause = elseClause;
    }
    
    public If(Expression condition, Statement thenClause) {
        this.condition = condition;
        this.thenClause = thenClause;
        this.elseClause = new Empty();
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    public boolean hasElseClause() {
        return !(elseClause instanceof Empty);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof If) {
            If that = (If)obj;
            return
                    this.condition.equals(that.condition) &&
                    this.thenClause.equals(that.thenClause) &&
                    this.elseClause.equals(that.elseClause);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (condition.hashCode() << 16) | (thenClause.hashCode() ^ elseClause.hashCode());
    }

    @Override
    public String toString() {
        if (hasElseClause()) {
            return "if " + condition + " then " + thenClause + " else " + elseClause;
        } else {
            return "if " + condition + " then " + thenClause;
        }
    }
}

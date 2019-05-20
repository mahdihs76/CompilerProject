package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Expression;
import parser.models.Statement;

public class While implements Statement {
    private final Expression head;
    private final Statement body;

    public While(Expression head, Statement body) {
        this.head = head;
        this.body = body;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof While) {
            While that = (While)obj;
            return
                    this.head.equals(that.head) &&
                    this.body.equals(that.body);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (head.hashCode() << 16) | body.hashCode();
    }

    @Override
    public String toString() {
        return "while " + head + " do " + body;
    }
}

package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Statement;
import utility.MyUtils;

import java.util.Collections;
import java.util.List;

public class Block implements Statement {
    private final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = Collections.unmodifiableList(statements);
    }

    public void accept(GraphVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Block) {
            Block that = (Block)obj;
            return this.statements.equals(that.statements);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return statements.hashCode();
    }

    @Override
    public String toString() {
        return "{ " + MyUtils.join(statements, "; ") + " }";
    }
}

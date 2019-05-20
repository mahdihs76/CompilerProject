package parser.models.statements;

import parser.models.GraphVisitor;
import parser.models.Statement;

public class Empty implements Statement {

    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Empty);
    }
    
    @Override
    public int hashCode() {
        return 2098345;
    }

    @Override
    public String toString() {
        return "{}";
    }
}

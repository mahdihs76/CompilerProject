package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Variable && this.name.equals(((Variable) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}

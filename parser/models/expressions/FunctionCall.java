package parser.models.expressions;

import parser.models.GraphVisitor;
import parser.models.Expression;
import utility.MyUtils;

import java.util.Arrays;
import java.util.List;

public class FunctionCall implements Expression {
    private final String functionName;
    private final List<Expression> arguments;

    public FunctionCall(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }
    
    public FunctionCall(String functionName, Expression... arguments) {
        this(functionName, Arrays.asList(arguments));
    }
    
    public void accept(GraphVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionCall) {
            FunctionCall that = (FunctionCall)obj;
            return
                    this.functionName.equals(that.functionName) &&
                    this.arguments.equals(that.arguments);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.functionName.hashCode() << 16) | arguments.hashCode();
    }

    @Override
    public String toString() {
        return functionName + "(" + MyUtils.join(arguments, ", ") + ")";
    }
}

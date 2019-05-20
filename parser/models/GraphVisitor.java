package parser.models;

import parser.models.expressions.*;
import parser.models.statements.*;

public abstract class GraphVisitor {
    public void visit(Block block) {}
    public void visit(Declaration declaration) {}
    public void visit(Assignment assignment) {}
    public void visit(Empty empty) {}
    
    public void visit(If ifStatement) {}
    public void visit(While whileOne) {}
    
    public void visit(BinaryOperation binaryOperation) {}
    public void visit(UnaryOperation unaryOperation) {}
    public void visit(FunctionCall call) {}
    public void visit(BoolConstant boolConstant) {}
    public void visit(IntConstant intConstant) {}
    public void visit(Variable variable) {}
}

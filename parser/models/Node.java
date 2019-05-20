package parser.models;

interface Node {
    void accept(GraphVisitor v);
}

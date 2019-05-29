package utility;

import java.util.Stack;

import models.Node;

class BinaryTree {
  
    private Node root;
      
    private void iterativePreorder()
    { 
        iterativePreorder(root); 
    } 
  
    private void iterativePreorder(Node node) {
          
        if (node == null) {
            return; 
        } 
  
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root); 
  
        while (!nodeStack.empty()) {
            Node mynode = nodeStack.peek();
            System.out.print(mynode.getLabel() + " ");
            nodeStack.pop();

            for (Node children : node.getChildrens()) {
                nodeStack.push(children);
            }
        }
    } 
  
    public static void main(String args[]) {
        BinaryTree tree = new BinaryTree();
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");
        Node nodeG = new Node("G");
        Node nodeH = new Node("H");
        Node nodeI = new Node("I");
        Node nodeJ = new Node("J");
        Node nodeK = new Node("K");
        tree.root = nodeA;
        tree.root.addChild(nodeB);
        tree.root.addChild(nodeC);
        nodeB.addChild(nodeD);
        nodeD.addChild(nodeG);
        nodeD.addChild(nodeH);
        nodeC.addChild(nodeE);
        nodeC.addChild(nodeF);
        nodeF.addChild(nodeI);
        nodeF.addChild(nodeJ);
        nodeF.addChild(nodeK);
        tree.iterativePreorder();
  
    } 
} 
package utility;

import java.util.ArrayList;
import java.util.Stack;

import models.Node;

class TreeHandler {
  
    private Node root;
      
    private void iterativePreOrder()
    { 
        iterativePreOrder(root);
    } 
  
    private void iterativePreOrder(Node node) {
          
        if (node == null) {
            return; 
        } 
  
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        while (!nodeStack.empty()) {
            Node myNode = nodeStack.peek();
            printNode(myNode);
            nodeStack.pop();

            ArrayList<Node> children = myNode.getChildrens();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node item = children.get(i);
                item.setDepth(myNode.getDepth() + 1);
                nodeStack.push(item);
            }
        }
    }

    private void printNode(Node node) {
        for (int i = 0; i < node.getDepth(); i++) {
            System.out.print("| ");
        }
        System.out.println(node.getLabel());
    }

    public static void main(String args[]) {
        TreeHandler tree = new TreeHandler();
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
        tree.iterativePreOrder();
  
    } 
} 
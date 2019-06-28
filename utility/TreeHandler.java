package utility;

import java.util.ArrayList;
import java.util.Stack;

import models.TreeNode;

public class TreeHandler {
  

    public static String iterativePreOrder(TreeNode treeRoot) {
          
        if (treeRoot == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
  
        Stack<TreeNode> treeNodeStack = new Stack<>();
        treeNodeStack.push(treeRoot);

        while (!treeNodeStack.empty()) {
            TreeNode myTreeNode = treeNodeStack.peek();

            sb.append(printNode(myTreeNode));
            sb.append("\n");

            treeNodeStack.pop();

            ArrayList<TreeNode> children = myTreeNode.getChildrens();
            for (int i = children.size() - 1; i >= 0; i--) {
                TreeNode item = children.get(i);
                item.setDepth(myTreeNode.getDepth() + 1 + myTreeNode.getLabel().length() / 2);
                treeNodeStack.push(item);
            }
        }
        return sb.toString();
    }

    public static String printNode(TreeNode treeNode) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < treeNode.getDepth(); i++) {
            sb.append("|    ");
        }
        sb.append(treeNode.getLabel());

        return sb.toString();
    }




    public static void main(String args[]) {
        TreeHandler tree = new TreeHandler();
        TreeNode treeNodeA = new TreeNode("A");
        TreeNode treeNodeB = new TreeNode("B");
        TreeNode treeNodeC = new TreeNode("C");
        TreeNode treeNodeD = new TreeNode("DDD");
        TreeNode treeNodeE = new TreeNode("E");
        TreeNode treeNodeF = new TreeNode("F");
        TreeNode treeNodeG = new TreeNode("G");
        TreeNode treeNodeH = new TreeNode("H");
        TreeNode treeNodeI = new TreeNode("I");
        TreeNode treeNodeJ = new TreeNode("J");
        TreeNode treeNodeK = new TreeNode("K");

        TreeNode root = treeNodeA;
        root.addChild(treeNodeB);
        root.addChild(treeNodeC);

        treeNodeB.addChild(treeNodeD);
        treeNodeD.addChild(treeNodeG);
        treeNodeD.addChild(treeNodeH);
        treeNodeC.addChild(treeNodeE);
        treeNodeC.addChild(treeNodeF);
        treeNodeF.addChild(treeNodeI);
        treeNodeF.addChild(treeNodeJ);
        treeNodeF.addChild(treeNodeK);

        System.out.println(iterativePreOrder(root));
  
    }




} 
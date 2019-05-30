package models;

import java.util.ArrayList;

public class TreeNode {
    private String label;
    private ArrayList<TreeNode> childrens;
    private int depth = 0;

    public TreeNode(String label) {
        this.label = label;
        childrens = new ArrayList<>();
    }

    public void addChild(TreeNode treeNode){
        childrens.add(treeNode);
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<TreeNode> getChildrens() {
        return childrens;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}

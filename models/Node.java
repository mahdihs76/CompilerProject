package models;

import java.util.ArrayList;

public class Node {
    private String label;
    private ArrayList<Node> childrens;

    public Node(String label) {
        this.label = label;
        childrens = new ArrayList<>();
    }

    public void addChild(Node node){
        childrens.add(node);
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<Node> getChildrens() {
        return childrens;
    }
}

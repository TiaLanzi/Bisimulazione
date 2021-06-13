package com.example.bisimulazione.directedgraph;

public class Edge {

    private Node one;
    private Node two;
    private int color;
    private boolean pathAlreadyExists;

    public Edge(Node one, Node two, int color, boolean pathAlreadyExists) {
        setOne(one);
        setTwo(two);
        setColor(color);
        pathAlreadyExists(pathAlreadyExists);
    }

    private void setOne(Node one) {
        this.one = one;
    }

    public Node getOne() {
        return this.one;
    }

    private void setTwo(Node two) {
        this.two = two;
    }

    public Node getTwo() {
        return this.two;
    }

    private void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    private void pathAlreadyExists(boolean pathAlreadyExists) {
        this.pathAlreadyExists = pathAlreadyExists;
    }

    public boolean getPathAlreadyExists() {
        return this.pathAlreadyExists;
    }

    public String toString() {
        return "First point: x: " + String.valueOf(this.getOne().getX()) + ", y: " + String.valueOf(this.getOne().getY()) +
                ", Second point: x: " + String.valueOf(this.getTwo().getX()) + ", y: " + String.valueOf(this.getTwo().getY()) + ", Color: " +
                String.valueOf(this.getColor()) + ", path already exists? " + this.getPathAlreadyExists();
    }
}

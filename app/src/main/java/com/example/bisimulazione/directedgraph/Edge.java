package com.example.bisimulazione.directedgraph;

public class Edge {

    private Node one;
    private Node two;

    public Edge() {
        super();
    }

    public Edge(Node one, Node two) {
        setOne(one);
        setTwo(two);
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

    public String toString() {
        return "First point: x: " + String.valueOf(this.getOne().getX()) + ", y: " + String.valueOf(this.getOne().getY()) +
                ", Second point: x: " + String.valueOf(this.getTwo().getX()) + ", y: " + String.valueOf(this.getTwo().getY()) + ".";
    }
}

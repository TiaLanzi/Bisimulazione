package com.example.bisimulazione.directedgraph;

public class Edge {

    private Node one;
    private Node two;
    private int color;
    private boolean toBottom;
    private boolean line;

    public Edge(Node one, Node two, int color, boolean toBottom, boolean line) {
        setOne(one);
        setTwo(two);
        setColor(color);
        setToBottom(toBottom);
        setLine(line);
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

    private void setToBottom(boolean toBottom) {
        this.toBottom = toBottom;
    }

    public boolean isToBottom() {
        return this.isToBottom();
    }

    private void setLine(boolean line) {
        this.line = line;
    }

    public boolean isLine() {
        return this.line;
    }

    public String toString() {
        return "First point: x: " + String.valueOf(this.getOne().getX()) + ", y: " + String.valueOf(this.getOne().getY()) +
                ", Second point: x: " + String.valueOf(this.getTwo().getX()) + ", y: " + String.valueOf(this.getTwo().getY()) + ", Color: " +
                String.valueOf(this.getColor()) + ", is to bottom? " + this.isToBottom() + ", verticalLine? " + this.isLine();
    }
}

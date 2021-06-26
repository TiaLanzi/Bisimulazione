package com.example.bisimulazione.directedgraph;

public class Edge {

    private int id;
    private Node one;
    private Node two;
    private int color;
    private boolean leftTable;
    private boolean toBottom;
    private boolean line;

    public Edge(int id, Node one, Node two, int color, boolean leftTable, boolean toBottom, boolean line) {
        setId(id);
        setOne(one);
        setTwo(two);
        setColor(color);
        setLeftTable(leftTable);
        setToBottom(toBottom);
        setLine(line);
    }
    private void setId(int id) {
        this.id = id;
    }

    protected int getId() {
        return this.id;
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

    private void setLeftTable(boolean leftTable) {
        this.leftTable = leftTable;
    }

    protected boolean isLeftTable() {
        return this.leftTable;
    }

    private void setToBottom(boolean toBottom) {
        this.toBottom = toBottom;
    }

    public boolean isToBottom() {
        return this.toBottom;
    }

    private void setLine(boolean line) {
        this.line = line;
    }

    public boolean isLine() {
        return this.line;
    }

    public String toString() {
        return "First point: x: " + this.getOne().getX()  + ", y: " + this.getOne().getY() +
                ", Second point: x: " + this.getTwo().getX() + ", y: " + this.getTwo().getY() + ", Color: " +
                this.getColor() + ", is to bottom? " + this.isToBottom() + ", verticalLine? " + this.isLine();
    }
}

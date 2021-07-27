package com.example.bisimulazione.directedgraph;

public class Edge {

    private int id;
    private Node source;
    private Node destination;
    private int color;
    private boolean leftTable;
    private boolean toBottom;
    private boolean line;

    public Edge(int id, Node source, Node destination, int color, boolean leftTable, boolean toBottom, boolean line) {
        setId(id);
        setSource(source);
        setDestination(destination);
        setColor(color);
        setLeftTable(leftTable);
        setToBottom(toBottom);
        setLine(line);
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    private void setSource(Node source) {
        this.source = source;
    }

    public Node getSource() {
        return this.source;
    }

    private void setDestination(Node destination) {
        this.destination = destination;
    }

    public Node getDestination() {
        return this.destination;
    }

    private void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    private void setLeftTable(boolean lTable) {
        this.leftTable = lTable;
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
        return "First point: x: " + this.getSource().getX() + ", y: " + this.getSource().getY() +
                ", Second point: x: " + this.getDestination().getX() + ", y: " + this.getDestination().getY() + ", Color: " +
                this.getColor() + ", is to bottom? " + this.isToBottom() + ", verticalLine? " + this.isLine();
    }
}

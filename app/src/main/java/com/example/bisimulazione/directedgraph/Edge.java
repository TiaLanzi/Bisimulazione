package com.example.bisimulazione.directedgraph;

import android.graphics.Color;

public class Edge {

    private Node one;
    private Node two;
    private int color;

    public Edge(Node one, Node two, int color) {
        setOne(one);
        setTwo(two);
        setColor(color);
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

    public String toString() {
        return "First point: x: " + String.valueOf(this.getOne().getX()) + ", y: " + String.valueOf(this.getOne().getY()) +
                ", Second point: x: " + String.valueOf(this.getTwo().getX()) + ", y: " + String.valueOf(this.getTwo().getY()) + ", Color: " +
                String.valueOf(this.getColor()) + ".";
    }
}

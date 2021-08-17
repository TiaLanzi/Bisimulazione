package com.example.bisimulazione.directedgraph;

import android.content.res.Resources;

public class Node {

    private static final String TAG = "Bisimulazione";

    private int id;
    private int x;
    private int y;
    private int color;
    private boolean leftTable;
    private boolean root;
    private boolean alreadyDrawn;
    private Edge[] outgoingEdges;

    public Node(int id, int x, int y, boolean root, boolean toLeft, boolean toRight, boolean leftTable, int color) {
        super();
        setId(id);
        int shiftHorizontal = 120;
        int shiftVertical = 200;
        if (root) {
            setX((Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 40);
            setY(y);
            setRoot(true);
        } else {
            setRoot(false);
            if (toLeft) {
                setX(x - shiftHorizontal);
                setY(y + shiftVertical);
            } else if (toRight) {
                setX(x + shiftHorizontal);
                setY(y + shiftVertical);
            } else {
                setX(x);
                setY(y + shiftVertical);
            }
        }
        setLeftTable(leftTable);
        setColor(color);
        setAlreadyDrawn(false);
        setOutgoingEdges(null);
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    private void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    private void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    private void setLeftTable(boolean leftTable) {
        this.leftTable = leftTable;
    }

    public boolean isLeftTable() {
        return this.leftTable;
    }

    private void setRoot(boolean root) {
        this.root = root;
    }

    protected void setAlreadyDrawn(boolean drawn) {
        this.alreadyDrawn = drawn;
    }

    public boolean isAlreadyDrawn() {
        return this.alreadyDrawn;
    }

    public void setOutgoingEdges(Edge[] outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }

    public Edge[] getOutgoingEdges() {
        return this.outgoingEdges;
    }
}
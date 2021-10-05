package com.example.bisimulazione.directedgraph;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.example.bisimulazione.R;

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

    private boolean isRoot() {
        return this.root;
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

    @NonNull
    @Override
    public String toString() {
        return "Node [ID: " + this.getId() + ", x: " + this.getX() + ", y: " + this.getY() + ", leftTable: "
                + this.isLeftTable() + ", root: " + this.isRoot() + ", colour: " + colourToString(this.getColor()) + "]";
    }

    private String colourToString(int colour) {
        String colore = "";
        switch (colour) {
            case -237502:
                colore = "red";
                break;
            case -16711895:
                colore = "green";
                break;
            case -13421773:
                colore = "black";
                break;
            case -15774591:
                colore = "blue";
                break;
            default:
                break;
        }
        return colore;
    }
}
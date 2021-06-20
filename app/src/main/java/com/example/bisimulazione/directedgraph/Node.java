package com.example.bisimulazione.directedgraph;

import android.content.res.Resources;

public class Node {

    private int x;
    private int y;
    private boolean root;
    private boolean toLeft;
    private boolean toRight;

    public Node(int x, int y, boolean root, boolean toLeft, boolean toRight, boolean leftTable) {
        super();
        int shiftHorizontal = 120;
        int shiftVertical = 200;
        if (root) {
            if (leftTable) {
                setX((Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 40);
            } else {
                setX((Resources.getSystem().getDisplayMetrics().widthPixels / (3 / 4)) + 40);
            }
            setY(y);
        } else {
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
        isRoot(root);
        isToLeft(toLeft);
        isToRight(toRight);
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

    private void isRoot(boolean root) {
        this.root = root;
    }

    public boolean isRoot() {
        return this.root;
    }

    private void isToLeft(boolean toLeft) {
        this.toLeft = toLeft;
    }

    public boolean isToLeft() {
        return this.toLeft;
    }

    private void isToRight(boolean toRight) {
        this.toRight = toRight;
    }

    public boolean isToRight() {
        return this.toRight;
    }
}
package com.example.bisimulazione.directedgraph;

import android.content.res.Resources;

public class Node {

    private int x;
    private int y;
    private boolean root;
    private boolean toLeft;
    private boolean toRight;
    private int shiftHorizontal = 96;
    private int shiftVertical = 112;

    public Node() {
        super();
        setX(0);
        setY(0);
        isRoot(false);
        isToLeft(false);
        isToRight(false);
    }

    public Node(int x, int y, boolean toLeft, boolean toRight) {
        super();
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
        isRoot(false);
        isToLeft(toLeft);
        isToRight(toRight);
    }

    public Node(int y, boolean root) {
        super();
        setX((Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 16);
        setY(y);
        isRoot(root);
        isToLeft(false);
        isToRight(false);
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
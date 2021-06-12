package com.example.bisimulazione.directedgraph;

public class Node {

    private int x;
    private int y;
    private boolean root;
    private boolean binary;
    private boolean toLeft;
    private boolean toRight;

    public Node() {
        super();
        setX(0);
        setY(0);
        isRoot(false);
        isBinary(false);
        isToLeft(false);
        isToRight(false);
    }

    public Node(int x, int y, boolean binary, boolean toLeft, boolean toRight) {
        super();
        if (binary) {
            if (toLeft) {
                setX(x - 128);
                setY(y + 128);
            } else {
                setX(x + 128);
                setY(y + 128);
            }
        } else {
            setX(x);
            setY(y + 128);
        }
        isRoot(false);
        isToLeft(toLeft);
        isToRight(toRight);
    }

    public Node(int x, int y, boolean binary, boolean root) {
        super();
        setX(x);
        setY(y);
        isRoot(root);
        isBinary(binary);
        isToLeft(false);
        isToRight(false);
    }

    public Node(int x, int y, boolean binary) {
        setX(x);
        setY(y);
        // useless control but ok let's put it
        if (!binary) {
            isBinary(binary);
            isRoot(false);
            isToLeft(false);
            isToRight(false);
        }
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

    private void isBinary(boolean binary) {
        this.binary = binary;
    }

    public boolean isBinary() {
        return this.binary;
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
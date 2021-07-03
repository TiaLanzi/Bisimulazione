package com.example.bisimulazione.directedgraph;

import android.content.res.Resources;

import com.example.bisimulazione.R;

public class Node {

    private final String TAG = "Bisimulazione";

    private int id;
    private int x;
    private int y;
    private boolean root;
    private boolean toLeft;
    private boolean toRight;
    private boolean alreadyDrawn;
    private int color = R.color.black;

    public Node(int id, int x, int y, boolean root, boolean toLeft, boolean toRight, boolean leftTable) {
        super();
        setId(id);
        int shiftHorizontal = 120;
        int shiftVertical = 200;
        if (root) {
            setColor(R.color.primaryColor);
            if (leftTable) {
                setX((Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 40);
            } else {
                //int shift = Resources.getSystem().getDisplayMetrics().widthPixels / 4;
                setX((Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 40);
                //Log.i(TAG, "xxxxxxxxxx" + String.valueOf(this.getX()));
            }
            setY(y);
            setRoot(true);
        } else {
            setColor(color);
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
        setAlreadyDrawn(false);
        setRoot(false);
        setToLeft(toLeft);
        setToRight(toRight);
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

    protected void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    private void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isRoot() {
        return this.root;
    }

    private void setToLeft(boolean toLeft) {
        this.toLeft = toLeft;
    }

    public boolean isToLeft() {
        return this.toLeft;
    }

    private void setToRight(boolean toRight) {
        this.toRight = toRight;
    }

    public boolean isToRight() {
        return this.toRight;
    }

    protected void setAlreadyDrawn(boolean drawn) {
        this.alreadyDrawn = drawn;
    }

    public boolean isAlreadyDrawn() {
        return this.alreadyDrawn;
    }
}
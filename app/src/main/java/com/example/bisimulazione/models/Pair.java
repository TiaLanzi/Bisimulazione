package com.example.bisimulazione.models;

import com.example.bisimulazione.directedgraph.Node;

public class Pair {

    private Node left;
    private Node right;

    public Pair(Node left, Node right) {
        setLeft(left);
        setRight(right);
    }

    private void setLeft(Node left) {
        if (left != null) {
            this.left = left;
        }
    }

    private Node getLeft() {
        return this.left;
    }

    private void setRight(Node right) {
        if (right != null) {
            this.right = right;
        }
    }

    private Node getRight() {
        return this.right;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            return pair.getLeft() == this.getLeft() && pair.getRight() == this.getRight();
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (this.getLeft().getId() + this.getRight().getId());
        return result;
    }
}

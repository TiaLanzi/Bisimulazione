package com.example.bisimulazione.algorithmvisualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Digraph {

    private Map<Integer, List<Integer>> neighbours = new HashMap<Integer, List<Integer>>();
    public double[][] directedArray;

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int v : neighbours.keySet())
            stringBuffer.append("\n   " + v + " -> " + neighbours.get(v));
        return stringBuffer.toString();
    }

    public void add(int vertex) {
        if (neighbours.containsKey(vertex)) return;
        neighbours.put(vertex, new ArrayList<Integer>());
    }

    public boolean contains(int vertex) {
        return neighbours.containsKey(vertex);
    }

    public void add(int from, int to) {
        this.add(from);
        this.add(to);
        neighbours.get(from).add(to);
    }

    public void remove(int from, int to) {
        if (!(this.contains(from) && this.contains(to))) {
            throw new IllegalArgumentException("Nonexistent vertex");
        }
        neighbours.get(from).remove(to);
    }

    public int getRoot() {
        return topSort().get(0);
    }

    public boolean exists(int from) {
        return neighbours.get(from) != null && neighbours.get(from).size() != 0;
    }

    public boolean edgeExists(int from, int to) {
        return neighbours.get(from) != null && neighbours.get(from).size() != 0 && neighbours.get(from).contains(to);
    }

    public List<Integer> getNeighbours(int node) {
        return neighbours.get(node);
    }

    public int size() {
        return neighbours.size();
    }

    public void setDirectedArray(double[][] directedArray) {
        this.directedArray = directedArray;
    }

    public Map<Integer, Integer> outDegree() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Integer v : neighbours.keySet()) result.put(v, neighbours.get(v).size());
        return result;
    }

    public Map<Integer, Integer> inDegree() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (Integer v : neighbours.keySet()) result.put(v, 0);
        for (Integer from : neighbours.keySet()) {
            for (Integer to : neighbours.get(from)) {
                result.put(to, (result.get(to) + 1));
            }
        }
        return result;
    }

    public List<Integer> topSort() {
        Map<Integer, Integer> degree = inDegree();
        Stack<Integer> zeroVerts = new Stack<Integer>();
        for (Integer v : degree.keySet()) {
            if (degree.get(v) == 0) {
                zeroVerts.push(v);
            }
        }
        List<Integer> result = new ArrayList<Integer>();
        while (!(zeroVerts.isEmpty())) {
            Integer v = zeroVerts.pop();
            result.add(v);
            for (Integer neighbour : neighbours.get(v)) {
                degree.put(neighbour, (degree.get(neighbour) - 1));
                if (degree.get(neighbour) == 0) {
                    zeroVerts.push(neighbour);
                }
            }
        }
        if (result.size() != neighbours.size()) {
            return null;
        }
        return result;
    }

    public boolean isAcyclic() {
        return topSort() != null;
    }
}

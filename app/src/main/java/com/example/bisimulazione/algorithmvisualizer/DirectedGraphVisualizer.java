package com.example.bisimulazione.algorithmvisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectedGraphVisualizer extends AlgorithmVisualizer {

    private Paint circle;
    private Paint text;
    private Paint line;
    private Paint circleHighlight;
    private Paint lineHighlight;
    private Rect bounds;

    private List<Integer> highlightNode = new ArrayList<>();
    private Map<Integer, List<Integer>> highlightLine = new HashMap<>();
    private Map<Integer, Point> pointMap = new HashMap<>();

    private Digraph digraph;
    private List<Integer> array = new ArrayList<>();

    public DirectedGraphVisualizer(Context context) {
        super(context);
        initialize();
    }

    public DirectedGraphVisualizer(Context context, AttributeSet attributes) {
        super(context, attributes);
        initialize();
    }

    private void initialize() {
        circle = new Paint();
        text = new Paint();

        text.setColor(Color.WHITE);
        text.setTextSize(getDimensionInPixelFromSp(16));
        text.setAntiAlias(true);
        text.setTextAlign(Paint.Align.CENTER);

        bounds = new Rect();
        text.getTextBounds("0", 0, 1, bounds);

        circle.setColor(Color.RED);
        circle.setAntiAlias(true);

        line = new Paint();
        line.setStrokeWidth(5);
        line.setColor(Color.BLACK);

        circleHighlight = new Paint(circle);
        circleHighlight.setColor(Color.BLUE);

        lineHighlight = new Paint(line);
        lineHighlight.setColor(Color.BLUE);
        lineHighlight.setStrokeWidth(10);
    }

    public void setData(Digraph digraph) {
        this.digraph = digraph;
        this.array = digraph.topSort();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (array != null && array.size() != 0) {
            drawGraph(canvas);
        }
    }

    private void drawGraph(Canvas canvas) {
        pointMap.clear();
        double[][] graphArray = digraph.directedArray;

        int root = 0;

        for (int i = 0; i < array.size(); i++) {
            double parentNode = graphArray[0][i];
            double numHorizontalNode = graphArray[3][i];
            double numVerticalNode = graphArray[4][i];
            double toLeftOfRoot = graphArray[5][i];

            Point point = new Point();
            Point p = new Point();

            p.x = (getWidth() / 2) + getDimensionInPixel(24);
            p.y = getDimensionInPixel(40);

            if (parentNode == root) {
                point.x = (getWidth() / 2) + getDimensionInPixel(24);
                point.y = getDimensionInPixel(40);
            } else if (toLeftOfRoot == 1) {
                point.x = (int) (p.x - numHorizontalNode * getDimensionInPixel(56));
                point.y = (int) (p.y + numVerticalNode * getDimensionInPixel(64));
            } else if (toLeftOfRoot == 0) {
                point.x = (int) (p.x + numHorizontalNode * getDimensionInPixel(56));
                point.y = (int) (p.y + numVerticalNode * getDimensionInPixel(64));
            }
            addNode(point, (int) parentNode);
        }
        drawNodes(canvas);
    }

    private void addNode(Point point, int i) {
        pointMap.put(i, point);
    }

    private void drawNodes(Canvas canvas) {
        for (Map.Entry<Integer, Point> entry : pointMap.entrySet()) {
            Integer key = entry.getKey();

            if (digraph.exists(key)) {
                List<Integer> edges = digraph.getNeighbours(key);
                for (Integer i : edges) {
                    if (pointMap.get(i) != null) {
                        drawNodeLine(canvas, key, i);
                    }
                }
            }
        }
        for (Map.Entry<Integer, Point> entry : pointMap.entrySet()) {
            Integer key = entry.getKey();
            Point value = entry.getValue();
            drawCircleTextNode(canvas, value, key);
        }
    }

    private void drawCircleTextNode(Canvas canvas, Point point, int number) {
        String num = String.valueOf(number);

        if (highlightNode.contains(number)) {
            canvas.drawCircle(point.x, point.y, getDimensionInPixel(16), circleHighlight);
        } else {
            canvas.drawCircle(point.x, point.y, getDimensionInPixel(16), circle);
        }
        int yOffset = bounds.height() / 2;

        canvas.drawText(num, point.x, (point.y + yOffset), text);
    }

    private void drawNodeLine(Canvas canvas, int s, int e) {
        Point start = pointMap.get(s);
        Point end = pointMap.get(e);

        int midX = (start.x + end.x) / 2;
        int midY = (start.y + end.y) / 2;

        boolean highlight = (highlightLine.containsKey(s) && highlightLine.get(s).contains(e));
        if (highlight) {
            canvas.drawLine(start.x, start.y, end.x, end.y, lineHighlight);
            canvas.drawLine(midX, midY, midX + getDimensionInPixel(4), midY - getDimensionInPixel(2), lineHighlight);
            canvas.drawLine(midX, midY, midX - getDimensionInPixel(4), midY - getDimensionInPixel(2), lineHighlight);
        } else {
            canvas.drawLine(start.x, start.y, end.x, end.y, line);
            canvas.drawLine(midX, midY, midX + getDimensionInPixel(4), midY - getDimensionInPixel(2), line);
            canvas.drawLine(midX, midY, midX + getDimensionInPixel(4), midY - getDimensionInPixel(2), line);
        }
    }

    public void highlightNode(int node) {
        this.highlightNode.add(node);
        invalidate();
    }

    public void highlightLine(int start, int end) {
        List<Integer> edges = highlightLine.get(start);
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(end);
        this.highlightLine.put(start, edges);
        invalidate();
    }

    @Override
    public void onCompleted() {
        highlightLine.clear();
        highlightNode.clear();
    }
}



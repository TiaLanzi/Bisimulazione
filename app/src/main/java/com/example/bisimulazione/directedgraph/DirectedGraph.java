package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

@SuppressLint("ViewConstructor")
public abstract class DirectedGraph extends View {

    protected static final String TAG = "Bisimulazione";

    private Edge[] edges;
    private Node[] nodes;

    protected final float stroke = 8f;
    protected final float radius = 40f;

    public DirectedGraph(Context context) {
        super(context);
    }

    public DirectedGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DirectedGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public Edge[] getEdges() {
        return this.edges;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return this.nodes;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected abstract void drawGraph(Canvas canvas);

    protected abstract void drawNodes(Canvas canvas);

    protected abstract void drawEdges(Canvas canvas);

    protected Paint paintNode(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    protected Paint paintLine(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    protected Paint paintTriangle(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(stroke);
        paint.setAntiAlias(true);
        return paint;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
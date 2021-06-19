package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.bisimulazione.R;
import com.example.bisimulazione.Table;

@SuppressLint("ViewConstructor")
public class DirectedGraph extends View {

    private static final String TAG = "Bisimulazione";

    private Paint paintRoot;
    private Paint paintVertex;
    private Paint paintArc;
    private Edge[] edges;
    private final float radius = 32f;
    private final float stroke = 8f;
    private RectF rect;
    private String horizontal;

    public DirectedGraph(Context context, Edge[] edges) {
        super(context);
        setEdges(edges);
        printEdges();
    }

    private void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public Edge[] getEdges() {
        return this.edges;
    }

    public void printEdges() {
        for (int i = 0; i < this.getEdges().length; i++) {
            if (this.getEdges()[i] != null) {
                String TAG = "Bisimulazione";
                Log.i(TAG, this.getEdges()[i].toString());
            }
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < this.getEdges().length; i++) {
            if (this.getEdges()[i] != null) {

                // coordinates of centre of first vertex
                Point pointOne = new Point(this.getEdges()[i].getOne().getX(), this.getEdges()[i].getOne().getY());
                // coordinates of centre of second vertex
                Point pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());
                // set string horziontal
                if (this.getEdges()[i].getTwo().isToLeft()) {
                    horizontal = "RL";
                } else {
                    horizontal = "LR";
                }
                // rectangle middle-top in point one and right in point two
                rect = getRectF(pointOne, pointTwo, horizontal);

                // paint for arc
                paintArc = paintArc(getResources().getColor(R.color.red));
                if (this.getEdges()[i].getTwo().isToLeft()) {
                    canvas.drawArc(rect, 180, 90, false, paintArc);
                } else {
                    canvas.drawArc(rect, 270, 90, false, paintArc);
                }



                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(rect, paint);

                // create paint for root
                paintRoot = paintRoot();
                // draw first Vertex if it doesn't exist
                if (!this.getEdges()[i].getPathAlreadyExists()) {
                    canvas.drawCircle(pointOne.x, pointOne.y, radius, paintRoot);
                }
                // draw second vertex if it doesn't exist
                paintVertex = paintVertex(getResources().getColor(R.color.black));
                if (!this.getEdges()[i].getPathAlreadyExists()) {
                    canvas.drawCircle(pointTwo.x, pointTwo.y, radius, paintVertex);
                }
            }
        }
    }

    private Paint paintRoot() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.primaryColor));
        return paint;
    }

    private Paint paintVertex(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    private Paint paintArc(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(stroke / 2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        return paint;
    }

    private RectF getRectF(Point pointOne, Point pointTwo, String horizontal) {
        /**
         * LR = left to right
         */
        RectF rectF = new RectF();
        if (horizontal.equals("LR")) {
            rectF = new RectF((pointOne.x / 2), pointOne.y, pointTwo.x, (pointTwo.y * 2));
        } else {
            rectF = new RectF((pointOne.x / 2), pointOne.y, pointTwo.x, (pointTwo.y * 2));
        }
        return rectF;
    }
}
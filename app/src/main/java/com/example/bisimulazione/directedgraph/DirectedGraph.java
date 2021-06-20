package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.bisimulazione.R;

@SuppressLint("ViewConstructor")
public class DirectedGraph extends View {

    private static final String TAG = "Bisimulazione";

    private Edge[] edges;
    private final float stroke = 8f;

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
                Log.i(TAG, "Point one x: " + String.valueOf(this.getEdges()[i].getOne().getX()));
                Log.i(TAG, "Point one y: " + String.valueOf(this.getEdges()[i].getOne().getY()));
                // coordinates of centre of second vertex
                Point pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());
                Log.i(TAG, "Point two x: " + String.valueOf(this.getEdges()[i].getTwo().getX()));
                Log.i(TAG, "Point two y: " + String.valueOf(this.getEdges()[i].getTwo().getY()));
                Log.i(TAG, "");
                if (this.getEdges()[i].isLine()) {
                    Path path = new Path();
                    path.reset();
                    path.moveTo(pointOne.x, pointOne.y);
                    path.lineTo(pointTwo.x, pointTwo.y);
                    Paint paintLine = paintLine(this.getEdges()[i].getColor());
                    canvas.drawPath(path, paintLine);
                } else {
                    // set string horziontal
                    String horizontal;
                    if (this.getEdges()[i].getTwo().isRoot()) {
                        if (this.getEdges()[i].getOne().isToLeft()) {
                            horizontal = "LR";
                        } else {
                            horizontal = "RL";
                        }
                    } else if (this.getEdges()[i].getTwo().isToLeft()) {
                        horizontal = "RL";
                    } else {
                        horizontal = "LR";
                    }
                    // set string vertical
                    String vertical;
                    if (this.getEdges()[i].isToBottom()) {
                        vertical = "TB";
                    } else {
                        vertical = "BT";
                    }
                    // rectangle middle-top in point one and right in point two
                    RectF rect = getRectF(pointOne, pointTwo, horizontal, vertical);
                    // paint for arc
                    Paint paintArc = paintArc(this.getEdges()[i].getColor());
                    if (vertical.equals("TB")) {
                        if (horizontal.equals("LR")) {
                            canvas.drawArc(rect, 270, 90, false, paintArc);
                        } else {
                            canvas.drawArc(rect, 180, 90, false, paintArc);
                        }
                    } else {
                        if (horizontal.equals("LR")) {
                            canvas.drawArc(rect, 0, 360, false, paintArc);
                        } else {
                            canvas.drawArc(rect, 0, 360, false, paintArc);
                        }
                    }
                    if (vertical.equals("BT")) {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(rect, paint);
                    }
                }
                // create paint for root
                Paint paintVertex = new Paint();
                if (this.getEdges()[i].getOne().isRoot() || this.getEdges()[i].getTwo().isRoot()) {
                    paintVertex = paintVertex(getResources().getColor(R.color.primaryColor));
                } else {
                    paintVertex = paintVertex(getResources().getColor(R.color.black));
                }
                float radius = 40f;
                // draw first vertex
                canvas.drawCircle(pointOne.x, pointOne.y, radius, paintVertex);
                // draw second vertex
                canvas.drawCircle(pointTwo.x, pointTwo.y, radius, paintVertex);
            }
        }
    }

    private Paint paintVertex(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    private Paint paintArc(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    private Paint paintLine(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    private RectF getRectF(Point pointOne, Point pointTwo, String horizontal, String vertical) {
        /**
         * LR = left to right
         * RL = right to left
         * TB = top to bottom
         * BT = bottom to top
         */
        RectF rectF = new RectF();
        if (vertical.equals("TB")) {
            if (horizontal.equals("LR")) {
                rectF = new RectF((pointOne.x / 2), pointOne.y, pointTwo.x, (pointTwo.y * 2));
            } else {
                rectF = new RectF(pointTwo.x, pointOne.y, (pointTwo.x * 2), pointTwo.y * 2);
            }
        } else {
            if (horizontal.equals("LR")) {
                rectF = new RectF(pointOne.x, pointOne.y, pointTwo.x, pointTwo.y);
            } else {
                rectF = new RectF(pointOne.x, pointOne.y, pointTwo.x, pointTwo.y);
            }
        }
        return rectF;
    }
}
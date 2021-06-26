package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.bisimulazione.R;

@SuppressLint("ViewConstructor")
public class DirectedGraph extends View {

    private static final String TAG = "Bisimulazione";

    private Edge[] edges;
    private final float stroke = 8f;
    private final float radius = 40f;

    private RectF rectF;

    private Paint paintNode;

    //private Canvas canvas;

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
                //Log.i(TAG, this.getEdges()[i].toString());
            }
        }
    }

    /*private void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    private Canvas getCanvas() {
        return this.canvas;
    }*/

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //this.setCanvas(canvas);

        drawGraph(canvas);
    }

    private Paint paintNode(int color) {
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

    private void drawGraph(Canvas canvas) {
        Paint paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRect.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < this.getEdges().length; i++) {
            if (this.getEdges()[i] != null) {
                // coordinates of centre of first vertex
                Point pointOne = new Point(this.getEdges()[i].getOne().getX(), this.getEdges()[i].getOne().getY());
                //Log.i(TAG, "Point one x: " + String.valueOf(this.getEdges()[i].getOne().getX()));
                //Log.i(TAG, "Point one y: " + String.valueOf(this.getEdges()[i].getOne().getY()));
                // coordinates of centre of second vertex
                Point pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());
                //Log.i(TAG, "Point two x: " + String.valueOf(this.getEdges()[i].getTwo().getX()));
                //Log.i(TAG, "Point two y: " + String.valueOf(this.getEdges()[i].getTwo().getY()));
                //Log.i(TAG, "");
                Paint paintArc = paintArc(this.getEdges()[i].getColor());
                Path path = new Path();
                path.reset();
                switch (this.getEdges()[i].getId()) {
                    case 1:
                        rectF = rectFOneTwo();
                        canvas.drawArc(rectF, 184 + (radius / 2), (86 - radius), false, paintArc);
                        //canvas.drawRect(rectF, paintRect);
                        break;
                    case 2:
                        rectF = rectFOneTwo();
                        canvas.drawArc(rectF, 268 + (radius / 2), (88 - radius), false, paintArc);
                        //canvas.drawRect(rectF, paintRect);
                        break;
                    case 3:
                        path.moveTo((pointOne.x + radius), pointOne.y);
                        path.lineTo((pointTwo.x - 12), (pointTwo.y + radius));
                        break;
                    case 4:
                        path.moveTo((pointOne.x - radius), pointOne.y);
                        path.lineTo((pointTwo.x + 12), (pointTwo.y + radius));
                        break;
                    case 5:
                    case 6:
                        path.moveTo(pointOne.x, (pointOne.y + radius));
                        path.lineTo(pointTwo.x, (pointTwo.y - radius));
                        break;
                    case 7:
                        path.moveTo((pointOne.x - radius), pointOne.y);
                        path.lineTo((pointTwo.x + radius), pointTwo.y);
                        break;
                    default:
                        break;
                }
                Paint paintLine = paintLine(this.getEdges()[i].getColor());
                canvas.drawPath(path, paintLine);
                // create paint for root;
                paintNode = paintNode(getEdges()[i].getOne().getColor());
                //Log.i(TAG, String.valueOf(getEdges()[i].getOne().getColor()));
                // draw first vertex if not already drawn
                if (!getEdges()[i].getOne().isAlreadyDrawn()) {
                    Log.i(TAG, "Draw circle one " + this.getEdges()[i].getOne().getId());
                    canvas.drawCircle(pointOne.x, pointOne.y, radius, paintNode);
                    // set already drawn
                    getEdges()[i].getOne().setAlreadyDrawn(true);
                }
                paintNode = paintNode(getEdges()[i].getTwo().getColor());
                //Log.i(TAG, String.valueOf(getEdges()[i].getTwo().getColor()));
                // draw second vertex if not already drawn
                if (!getEdges()[i].getTwo().isAlreadyDrawn()) {
                    Log.i(TAG, "Draw circle two " + this.getEdges()[i].getTwo().getId());
                    canvas.drawCircle(pointTwo.x, pointTwo.y, radius, paintNode);
                    // set already drawn
                    getEdges()[i].getTwo().setAlreadyDrawn(true);
                }
            }
        }

    }

    private RectF rectFOneTwo() {
        Point pointOne = new Point(this.getEdges()[1].getOne().getX(), this.getEdges()[1].getOne().getY());
        Point pointTwo = new Point(this.getEdges()[1].getTwo().getX(), this.getEdges()[1].getTwo().getY());
        rectF = new RectF((pointOne.x / 2), pointOne.y, pointTwo.x, (pointTwo.y * 2));
        return rectF;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        paintNode = paintNode(getResources().getColor(R.color.primaryColor));
        super.onTouchEvent(event);
        for (Edge edge : this.getEdges()) {
            if (edge != null) {
                if (edge.getOne() != null && edge.getTwo() != null) {
                    boolean isTouched = touchIsInCircle(event.getX(), event.getY(), edge.getOne().getX(), edge.getOne().getY(), radius);
                    if (isTouched) {
                        Log.i(TAG, "Circle touched");
                        //canvas.drawCircle(edge.getTwo().getX(), edge.getTwo().getY(), radius, paintVertex);
                        //edge.getTwo().setColor(getResources().getColor(R.color.primaryColor));
                        invalidate();
                        //onDraw(this.getCanvas());
                    }
                }
            }
        }
        return true;
    }

    private boolean touchIsInCircle(float x, float y, float centreX, float centreY, float radius) {
        double dx = Math.pow(x - centreX, 2);
        double dy = Math.pow(y - centreY, 2);

        return (dx + dy) < Math.pow(radius, 2);
    }
}
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
    }

    private void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public Edge[] getEdges() {
        return this.edges;
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
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    private Paint paintLine(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(stroke);
        paint.setColor(color);
        return paint;
    }

    private Paint paintTriangle(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(stroke);
        paint.setAntiAlias(true);
        return paint;
    }

    private void drawGraph(Canvas canvas) {
        Paint paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRect.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < this.getEdges().length; i++) {
            if (this.getEdges()[i] != null) {
                // coordinates of centre of first vertex
                Point pointOne = new Point(this.getEdges()[i].getOne().getX(), this.getEdges()[i].getOne().getY());
                // coordinates of centre of second vertex
                Point pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());
                Point endPoint;
                Point a;
                Point b;
                Point c;
                Paint paintLine = paintLine(this.getEdges()[i].getColor());
                Paint paintTriangle = paintTriangle(this.getEdges()[i].getColor());
                Path path = new Path();
                path.reset();
                float arrowHead = 16f;
                float shiftArrowHead = 4f;
                if (this.getEdges()[i].isLeftTable()) {
                    switch (this.getEdges()[i].getId()) {
                        case 1:
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius - stroke)));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead), ((int) (endPoint.y - arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.moveTo(((b.x + c.x) / 2f), ((b.y + c.y) / 2f));
                            path.lineTo((pointOne.x - radius - (stroke / 2)), pointOne.y);
                            canvas.drawPath(path, paintLine);
                            path.reset();
                            break;
                        case 2:
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius - stroke)));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead), ((int) (endPoint.y - arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.moveTo(((b.x + c.x) / 2f), ((b.y + c.y) / 2f));
                            path.lineTo((pointOne.x + radius + (stroke / 2)), pointOne.y);
                            canvas.drawPath(path, paintLine);
                            path.reset();
                            break;
                        case 3:
                            path.moveTo((pointOne.x + radius + (stroke / 2)), pointOne.y);
                            path.lineTo((pointTwo.x - 16), (pointTwo.y + radius));
                            endPoint = new Point((pointTwo.x - 16), (int) (pointTwo.y + radius));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead), ((int) (endPoint.y + arrowHead - shiftArrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead - (shiftArrowHead * 2)), ((int) (endPoint.y + arrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 4:
                            path.moveTo((pointOne.x - radius - (stroke / 2)), pointOne.y);
                            path.lineTo((pointTwo.x + 16), (pointTwo.y + radius));
                            endPoint = new Point((pointTwo.x + 16), (int) (pointTwo.y + radius));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead + (shiftArrowHead * 2)), ((int) (endPoint.y + arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y + arrowHead - shiftArrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 5:
                        case 6:
                            path.moveTo(pointOne.x, (pointOne.y + radius));
                            path.lineTo(pointTwo.x, (pointTwo.y - radius));
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius)));
                            a = new Point(endPoint.x, ((int) (endPoint.y - stroke)));
                            b = new Point(((int) (endPoint.x - arrowHead)), ((int) (endPoint.y - arrowHead)));
                            c = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, (endPoint.y - stroke));
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 7:
                            path.moveTo((pointOne.x - radius), pointOne.y);
                            path.lineTo((pointTwo.x + radius), pointTwo.y);
                            endPoint = new Point(((int) (pointTwo.x + radius)), pointTwo.y);
                            a = new Point(((int) (endPoint.x + stroke)), endPoint.y);
                            b = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y + arrowHead)));
                            c = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y - arrowHead)));
                            path.moveTo((endPoint.x + stroke), endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (this.getEdges()[i].getId()) {
                        case 1:
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius - stroke)));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead), ((int) (endPoint.y - arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.moveTo(((b.x + c.x) / 2f), ((b.y + c.y) / 2f));
                            path.lineTo((pointOne.x - radius - (stroke / 2)), pointOne.y);
                            canvas.drawPath(path, paintLine);
                            path.reset();
                            break;
                        case 2:
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius - stroke)));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead), ((int) (endPoint.y - arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.moveTo(((b.x + c.x) / 2f), ((b.y + c.y) / 2f));
                            path.lineTo((pointOne.x + radius + (stroke / 2)), pointOne.y);
                            canvas.drawPath(path, paintLine);
                            path.reset();
                            break;
                        case 3:
                            path.moveTo((pointOne.x - radius - (stroke / 2)), pointOne.y);
                            path.lineTo((pointTwo.x + 16), (pointTwo.y + radius));
                            endPoint = new Point((pointTwo.x + 16), (int) (pointTwo.y + radius));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead + (shiftArrowHead * 2)), ((int) (endPoint.y + arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead), ((int) (endPoint.y + arrowHead - shiftArrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 4:
                            path.moveTo((pointOne.x - radius), (pointOne.y - radius + 16));
                            path.lineTo((pointTwo.x - 20), (pointTwo.y + radius));
                            endPoint = new Point((pointTwo.x - 20), (int) (pointTwo.y + radius));
                            a = new Point(endPoint.x, endPoint.y);
                            b = new Point((int) (endPoint.x - arrowHead + (shiftArrowHead * 2)), ((int) (endPoint.y + arrowHead)));
                            c = new Point((int) (endPoint.x + arrowHead - shiftArrowHead), ((int) (endPoint.y + arrowHead - shiftArrowHead)));
                            path.moveTo(endPoint.x, endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 5:
                        case 7:
                            path.moveTo(pointOne.x, (pointOne.y + radius));
                            path.lineTo(pointTwo.x, (pointTwo.y - radius));
                            endPoint = new Point(pointTwo.x, ((int) (pointTwo.y - radius)));
                            a = new Point(endPoint.x, ((int) (endPoint.y - stroke)));
                            b = new Point(((int) (endPoint.x - arrowHead)), ((int) (endPoint.y - arrowHead)));
                            c = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y - arrowHead)));
                            path.moveTo(endPoint.x, (endPoint.y - stroke));
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        case 6:
                            path.moveTo((pointOne.x - radius), pointOne.y);
                            path.lineTo((pointTwo.x + radius), pointTwo.y);
                            endPoint = new Point(((int) (pointTwo.x + radius)), pointTwo.y);
                            a = new Point(((int) (endPoint.x + stroke)), endPoint.y);
                            b = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y + arrowHead)));
                            c = new Point(((int) (endPoint.x + arrowHead)), ((int) (endPoint.y - arrowHead)));
                            path.moveTo((endPoint.x + stroke), endPoint.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            canvas.drawPath(path, paintTriangle);
                            path.reset();
                            break;
                        default:
                            break;
                    }
                }
                canvas.drawPath(path, paintLine);
                // create paint for root;
                paintNode = paintNode(getEdges()[i].getOne().getColor());
                // draw first vertex if not already drawn
                if (!getEdges()[i].getOne().isAlreadyDrawn()) {
                    canvas.drawCircle(pointOne.x, pointOne.y, radius, paintNode);
                    // set already drawn
                    getEdges()[i].getOne().setAlreadyDrawn(true);
                }
                paintNode = paintNode(getEdges()[i].getTwo().getColor());
                // draw second vertex if not already drawn
                if (!getEdges()[i].getTwo().isAlreadyDrawn()) {
                    canvas.drawCircle(pointTwo.x, pointTwo.y, radius, paintNode);
                    // set already drawn
                    getEdges()[i].getTwo().setAlreadyDrawn(true);
                }
            }
        }
    }

    private RectF rectFOneTwo() {
        Point pointOne;
        Point pointTwo;
        pointOne = new Point(this.getEdges()[1].getOne().getX(), this.getEdges()[1].getOne().getY());
        pointTwo = new Point(this.getEdges()[1].getTwo().getX(), this.getEdges()[1].getTwo().getY());
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
package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("ViewConstructor")
public class DirectedGraph extends View {

    private static final String TAG = "Bisimulazione";

    private Edge[] edges;
    private Node[] nodes;

    private final float stroke = 8f;
    private final float radius = 40f;

    public DirectedGraph(Context context) {
        super(context);
    }

    public DirectedGraph(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DirectedGraph(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
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
        //canvas.save();
        //this.setCanvas(canvas);
        drawGraph(canvas);

        //canvas.restore();
        //invalidate();
    }

    private void drawGraph(Canvas canvas) {
        drawNodes(canvas);
        drawEdges(canvas);
    }

    private void drawNodes(Canvas canvas) {
        // draw nodes if not exist
        if (this.getNodes() != null) {
            for (Node node : this.getNodes()) {
                Paint paintNode = paintNode(node.getColor());
                if (!node.isAlreadyDrawn()) {
                    canvas.drawCircle(node.getX(), node.getY(), radius, paintNode);
                    node.setAlreadyDrawn(true);
                }
            }
        }
    }

    private void drawEdges(Canvas canvas) {
        if (this.getEdges() != null) {
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
                }
            }
        }
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

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
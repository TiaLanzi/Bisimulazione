package com.example.bisimulazione.directedgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;

import interfaces.CallbackNodeColor;

public class DirectedGraphLeft extends DirectedGraph implements CallbackNodeColor {

    public DirectedGraphLeft(Context context) {
        super(context);
    }

    public DirectedGraphLeft(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DirectedGraphLeft(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGraph(canvas);
    }

    @Override
    protected void drawGraph(Canvas canvas) {
        drawEdges(canvas);
        drawNodes(canvas);
    }

    @Override
    protected void drawNodes(Canvas canvas) {
        if (this.getNodes() != null) {
            for (Node node : this.getNodes()) {
                if (node != null) {
                    if (!node.isAlreadyDrawn()) {
                        canvas.drawCircle(node.getX(), node.getY(), radius, paintNode(node.getColor()));
                        node.setAlreadyDrawn(true);
                    }
                }
            }
            for (Node node : this.getNodes()) {
                node.setAlreadyDrawn(false);
            }
        }
    }

    @Override
    protected void drawEdges(Canvas canvas) {
        if (this.getEdges() != null) {
            for (int i = 0; i < this.getEdges().length; i++) {
                if (this.getEdges()[i] != null) {
                    // coordinates of centre of first vertex
                    Point pointOne = new Point(this.getEdges()[i].getSource().getX(), this.getEdges()[i].getSource().getY());
                    // coordinates of centre of second vertex
                    Point pointTwo = new Point(this.getEdges()[i].getDestination().getX(), this.getEdges()[i].getDestination().getY());
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
                    }
                }
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void onCallbackNodeOneColour(String color) {

    }

    @Override
    public void onCallbackNodeTwoColour(String color) {

    }

    @Override
    public void onCallbackNodeThreeColour(String color) {

    }

    @Override
    public void onCallbackNodeFourColour(String color) {

    }

    @Override
    public void onCallbackNodeFiveColour(String color) {

    }
}
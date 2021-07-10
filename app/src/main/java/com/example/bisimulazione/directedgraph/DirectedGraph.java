package com.example.bisimulazione.directedgraph;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bisimulazione.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import interfaces.CallbackColor;

@SuppressLint("ViewConstructor")
public class DirectedGraph extends View {

    private static final String TAG = "Bisimulazione";

    private Edge[] edges;
    private Node[] nodes;

    private String roomName;

    private final float stroke = 8f;
    private final float radius = 40f;

    private Paint paintNode;

    //private Canvas canvas;

    public DirectedGraph(Context context) {
        super(context);
    }

    public DirectedGraph(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DirectedGraph(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public DirectedGraph(Context context, Edge[] edges, Node[] nodes, String roomName) {
        super(context);
        setEdges(edges);
        for (Edge edge : edges) {
            Log.i(TAG, "2 - Edge " + edge.getId());
        }
        setNodes(nodes);
        for (Node node : nodes) {
            Log.i(TAG, "2 - Nodes " + node.getId());
        }
        setRoomName(roomName);
    }

    /*private void setActivity(Activity activityTable) {
        this.activityTable = activityTable;
    }

    public Activity getActivityTable() {
        return this.activityTable;
    } */

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

    private void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return this.roomName;
    }

    /*private void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    private Canvas getCanvas() {
        return this.canvas;
    }*/

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //this.setCanvas(canvas);
        drawGraph(canvas);

        canvas.restore();
    }

    private void drawGraph(Canvas canvas) {
        drawNodes(canvas);
        drawEdges(canvas);
    }

    private void drawNodes(Canvas canvas) {
        // draw nodes if not exist
        if (this.getNodes() != null) {
            for (Node node : this.getNodes()) {
                paintNode = paintNode(node.getColor());
                if (node.isLeftTable()) {
                    if (!node.isAlreadyDrawn()) {
                        canvas.drawCircle(node.getX(), node.getY(), radius, paintNode);
                        node.setAlreadyDrawn(true);
                    }
                } else {
                    if (!node.isAlreadyDrawn()) {
                        canvas.drawCircle(node.getX(), node.getY(), radius, paintNode);
                        Log.i(TAG, "2 - Drawn node " + node.getId());
                        node.setAlreadyDrawn(true);
                    }
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
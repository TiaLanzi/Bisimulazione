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

    private Paint paint;
    private Edge[] edges;
    private float radius = 32;
    private int shiftVertical = 32;
    private int shiftHorizontal = 8;
    private int side = (int) (radius * 2);
    private RectF rectOne;
    private RectF rectTwo;

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
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStrokeWidth((float) 6);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.primaryColor));

                Path path = new Path();
                //Path.Direction direction = new Path.Direction(Path.Direction.CCW);

                Point pointOne = new Point(this.getEdges()[i].getOne().getX(), this.getEdges()[i].getOne().getY());
                Point pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());

                // draw first Vertex
                if (!this.getEdges()[i].getPathAlreadyExists()) {
                    canvas.drawCircle(pointOne.x, pointOne.y, radius, paint);
                }

                Log.i(TAG, "Equals? " + String.valueOf(pointOne.x == (Resources.getSystem().getDisplayMetrics().widthPixels / 4) - 16));
                rectOne = new RectF((pointOne.x - radius), (pointOne.y - radius), side, side);
                rectTwo = new RectF((pointTwo.x - radius), (pointTwo.y - radius), side, side);

                // draw the edge
                path.reset();
                //path.moveTo(pointOne.x, pointOne.y);
                /*
                if (!this.getEdges()[i].getPathAlreadyExists()) {
                    canvas.drawPath(path, paint);
                } else {
                    // NON FUNZIONA!!!
                    RectF oval = new RectF();
                    oval.set(pointOne.x - radius, pointOne.y - radius, pointTwo.x + radius, pointTwo.y + radius);
                    int startAngle = (int) (180 / Math.PI * Math.atan2(pointOne.y - pointTwo.y, pointOne.x - pointTwo.x));
                    path.arcTo(oval, startAngle, -(float) 600, true);
                    canvas.drawArc(oval, startAngle, startAngle, false, paint);
                }*/

                // draw second vertex
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.black));
                if (!this.getEdges()[i].getPathAlreadyExists()) {
                    canvas.drawCircle(pointTwo.x, pointTwo.y, 32, paint);
                }

                path.arcTo(rectOne, 0, 90, true);
                path.arcTo(rectTwo, 0, 270, true);
                canvas.drawPath(path, paint);
            }
        }
    }
}
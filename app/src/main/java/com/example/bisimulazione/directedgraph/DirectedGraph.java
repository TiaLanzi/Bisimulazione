package com.example.bisimulazione.directedgraph;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.bisimulazione.R;
import com.example.bisimulazione.Table;

public class DirectedGraph extends View {

    private final String TAG = "Bisimulazione";

    private Paint paint;
    private Path path;
    private Point pointOne;
    private Point pointTwo;
    private Edge[] edges;

    public DirectedGraph(Context context, Edge[] edges) {
        super(context);
        setEdges(edges);
        printEdges();
    }

    private void setPointOne(Node one) {
        this.pointOne = new Point(one.getX(), one.getY());
    }

    public Point getPointOne() {
        return this.pointOne;
    }

    private void setPointTwo(Node two) {
        this.pointTwo = new Point(two.getX(), two.getY());
    }

    public Point getPointTwo() {
        return this.pointTwo;
    }

    private void setPath() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth((float) 8);

        path = new Path();
    }

    public Path getPath() {
        return this.path;
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
                Log.i(TAG, this.getEdges()[i].toString());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < this.getEdges().length; i++) {
            if (this.getEdges()[i] != null) {
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStrokeWidth((float) 6);

                path = new Path();

                pointOne = new Point(this.getEdges()[i].getOne().getX(), this.getEdges()[i].getOne().getY());
                pointTwo = new Point(this.getEdges()[i].getTwo().getX(), this.getEdges()[i].getTwo().getY());

                // draw first Vertex
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.primaryColor));
                canvas.drawCircle(pointOne.x, pointOne.y, 60, paint);

                // draw the edge
                path.reset();
                path.moveTo(pointOne.x, pointOne.y);
                path.lineTo(pointTwo.x, pointTwo.y);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.black));
                canvas.drawPath(path, paint);

                // draw second vertex
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.black));
                canvas.drawCircle(pointTwo.x, pointTwo.y, 60, paint);
            }
        }



        /*paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth((float) 6);

        path = new Path();

        pointOne = new Point(one.getX(), one.getY());
        pointTwo = new Point(two.getX(), two.getY());

        // draw first Vertex
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.primaryColor));
        canvas.drawCircle(pointOne.x, pointOne.y, 60, paint);

        // draw the edge
        path.reset();
        path.moveTo(pointOne.x, pointOne.y);
        path.lineTo(pointTwo.x, pointTwo.y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawPath(path, paint);

        // draw second vertex
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawCircle(pointTwo.x, pointTwo.y, 60, paint);*/
    }

    public void drawPath(Point one, Point two, Path path, Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.primaryColor));
        canvas.drawCircle(one.x, one.y, 60, paint);

        // draw the edge
        path.reset();
        path.moveTo(one.x, one.y);
        path.lineTo(two.x, two.y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawPath(path, paint);

        // draw second vertex
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawCircle(two.x, two.y, 60, paint);
    }

    private int getDimensionInPixel(int dp) {
        int density = getResources().getDisplayMetrics().densityDpi;

        int modifiedDp = dp;
        switch (density) {
            case DisplayMetrics
                    .DENSITY_LOW:
                modifiedDp = dp - (dp / 2);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                modifiedDp = dp - (dp / 3);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                modifiedDp = dp - (dp / 4);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_XXXHIGH:
                modifiedDp = dp;
                break;
            default:
                break;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, modifiedDp, getResources().getDisplayMetrics());
    }
}
package com.example.bisimulazione.directedgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.example.bisimulazione.R;

public class DirectedGraph extends View {

    private Paint paint;
    private Path path;
    private Point pointOne;
    private Point pointTwo;

    public DirectedGraph(Context context, Node one, Node two) {
        super(context);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth((float) 8);

        path = new Path();

        pointOne = new Point(one.getX(), one.getY());
        pointTwo = new Point(two.getX(), two.getY());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

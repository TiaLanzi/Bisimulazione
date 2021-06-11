package com.example.bisimulazione.algorithmvisualizer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public abstract class AlgorithmVisualizer extends View {

    public AlgorithmVisualizer(Context context) {
        super(context);
    }

    public AlgorithmVisualizer(Context context, AttributeSet
            attributes) {
        super(context, attributes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasurespec) {
        super.onMeasure(widthMeasureSpec, heightMeasurespec);
        setMeasuredDimension(getMeasuredWidth(), getDimensionInPixel(330));
    }

    public int getDimensionInPixel(int dp) {
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

    public int getDimensionInPixelFromSp(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    public abstract void onCompleted();
}

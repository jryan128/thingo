package com.joysignalgames.bazingo.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TestView extends ViewGroup {

    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int n = getNumberOfRowsAndCols();
        if (n > 0) {
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;
            measureChildren(View.MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: could probably make more efficient
        // FIXME: put into onMeasure
        int n = getNumberOfRowsAndCols();
        if (n > 0) {
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;
            for (int row=0; row < n; ++row) {
                for (int col=0; col < n; ++col) {
                    getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), h * (row + 1));
                }
            }
        }
    }

    private int getNumberOfRowsAndCols() {
        return (int) Math.floor(Math.sqrt(getChildCount()));
    }

    // FIXME: do onSaveInstance...
}

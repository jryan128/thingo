package com.joysignalgames.bazingo.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.joysignalgames.bazingo.app.BoardSquareButton;

public class BoardView extends ViewGroup {

    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setId(100);
        createBoardSquares();
    }

    private void createBoardSquares() {
        for (int i = 0; i < 25; i++) {
            BoardSquareButton square = new BoardSquareButton(getContext());
            square.setId(i);
            addView(square);
        }
    }

    /**
     * We tell every child exactly what size it needs to be.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int n = getNumberOfRowsAndCols();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (n > 0) {
            int w = width / n;
            int h = height / n;
            for (int i=0; i < getChildCount(); ++i) {
                // tell the child exactly what size it needs to be, screw you
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: could probably make more efficient
        int n = getNumberOfRowsAndCols();
        if (n > 0) {
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;
            for (int row=0; row < n; ++row) {
                for (int col=0; col < n; ++col) {
                    // TODO: should use values from onMeasure, but who cares, we know what they are
                    getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), h * (row + 1));
                }
            }
        }
    }

    private int getNumberOfRowsAndCols() {
        return (int) Math.floor(Math.sqrt(getChildCount()));
    }
}

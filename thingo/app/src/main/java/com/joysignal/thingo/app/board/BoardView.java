package com.joysignal.thingo.app.board;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import com.joysignal.thingo.app.R;

public class BoardView extends ViewGroup {
    private static final int NUMBER_OF_SQUARES = 25;
    public final Handler handler = new Handler();

    public BoardView(Context context) {
        super(context);
        setId(R.id.boardView); // have to set an id, or we won't get saving
        createBoardSquares();
    }

    private void createBoardSquares() {
        for (int i = 0; i < NUMBER_OF_SQUARES; i++) {
            BoardSquareButton square = new BoardSquareButton(getContext());
            // FIXME: find out if setting the id to a number like this is really okay
            // the only thing I can imagine going wrong is if the ids conflict with
            // something else and the saved state reloading goes wrong
            square.setId(i);
            addView(square);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount(); // TODO: maybe use NUMBER_OF_SQUARES, put an assert that it's equal to getChildCount?
        int n = getNumberOfRowsAndCols(childCount);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (n > 0) {
            int w = width / n;
            int h = height / n;
            for (int i = 0; i < childCount; ++i) {
                // tell the child exactly what size it needs to be, screw you
                getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: entire method could probably be more efficient
        int n = getNumberOfRowsAndCols(getChildCount());
        if (n > 0) {
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;
            for (int row = 0; row < n; ++row) {
                for (int col = 0; col < n; ++col) {
                    // TODO: should use values from onMeasure, but who cares, we know what they are
                    // TODO: some calcs probably could be made more efficient with addition?
                    if (row == (n - 1) && h >= w) {
                        getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), b);
                    } else if (col == (n - 1) && w >= h) {
                        getChildAt((row * n) + col).layout(w * col, h * row, r, h * (row + 1));
                    } else {
                        getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), h * (row + 1));
                    }
                }
            }
        }
    }

    private int getNumberOfRowsAndCols(int totalChildCount) {
        // TODO: maybe use NUMBER_OF_SQUARES, put an assert that it's equal to getChildCount?
        // TODO: sqrt?! really inefficient, should be cached with the value from NUMBER_OF_SQUARES
        return (int) Math.floor(Math.sqrt(totalChildCount));
    }
}

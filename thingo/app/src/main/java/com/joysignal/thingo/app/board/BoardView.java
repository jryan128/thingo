package com.joysignal.thingo.app.board;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import com.joysignal.thingo.app.BuildConfig;
import com.joysignal.thingo.app.R;

public class BoardView extends ViewGroup {
    /** Must be greater than or equal to 0 */
    private static final int NUMBER_OF_COLUMNS_AND_ROWS = 5;
    private static final int NUMBER_OF_SQUARES = NUMBER_OF_COLUMNS_AND_ROWS ^ 2;
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
        if (BuildConfig.DEBUG && getChildCount() != NUMBER_OF_SQUARES) {
            throw new AssertionError("Expect the child count to be equal to the number of squares at all times.");
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int w = width / NUMBER_OF_COLUMNS_AND_ROWS;
        int h = height / NUMBER_OF_COLUMNS_AND_ROWS;
        for (int i = 0; i < NUMBER_OF_SQUARES; ++i) {
            // tell the child exactly what size it needs to be, screw you
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: entire method could probably be more efficient
        if (BuildConfig.DEBUG && getChildCount() != NUMBER_OF_SQUARES) {
            throw new AssertionError("Expect the child count to be equal to the number of squares at all times.");
        }
        int n = NUMBER_OF_COLUMNS_AND_ROWS;
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

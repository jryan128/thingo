package io.jryan.thingo;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

public class BoardView extends ViewGroup {
    public final Handler handler = new Handler();

    public BoardView(Context context) {
        super(context);
        setId(R.id.boardView); // have to set an id, or we won't get saving
        createBoardSquares();
    }

    private void createBoardSquares() {
        for (int i = 0; i < Board.NUMBER_OF_SQUARES; i++) {
            BoardSquareButton square = new BoardSquareButton(getContext());
            // FIXME, possible collisions?
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                square.setId(View.generateViewId());
//            } else {
            square.setId(i);
//            }
            addView(square);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (BuildConfig.DEBUG && getChildCount() != Board.NUMBER_OF_SQUARES) {
            throw new AssertionError("Expect the child count to be equal to the number of squares at all times.");
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
//
        int w = width / Board.NUMBER_OF_COLUMNS_AND_ROWS;
        int h = height / Board.NUMBER_OF_COLUMNS_AND_ROWS;
        for (int i = 0; i < Board.NUMBER_OF_SQUARES; ++i) {
            // tell the child exactly what size it needs to be, screw you
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: entire method could probably be more efficient
        if (BuildConfig.DEBUG && getChildCount() != Board.NUMBER_OF_SQUARES) {
            throw new AssertionError("Expected the child count to be equal to the number of squares at all times.");
        }
        int n = Board.NUMBER_OF_COLUMNS_AND_ROWS;
        int w = getMeasuredWidth() / n;
        int h = getMeasuredHeight() / n;
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                // TODO: should use values from onMeasure, but who cares, we know what they are
                // TODO: some calcs probably could be made more efficient with addition?
                View child = getChildAt((row * n) + col);
                if (row == (n - 1) && h >= w) {
                    child.layout(w * col, h * row, w * (col + 1), b);
                } else if (col == (n - 1) && w >= h) {
                    child.layout(w * col, h * row, r, h * (row + 1));
                } else {
                    child.layout(w * col, h * row, w * (col + 1), h * (row + 1));
                }
            }
        }
    }
}

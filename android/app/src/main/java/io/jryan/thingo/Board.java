package io.jryan.thingo;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board extends ViewGroup {
    /**
     * NUMBER_OF_COLUMNS_AND_ROWS must be greater than or equal to 0
     */
    static final int NUMBER_OF_COLUMNS_AND_ROWS = 5;
    static final int NUMBER_OF_SQUARES = NUMBER_OF_COLUMNS_AND_ROWS * NUMBER_OF_COLUMNS_AND_ROWS;

    public Board(Context context) {
        super(context);
        setId(R.id.boardView); // have to set an id, or we won't get saving
        for (int i = 0; i < Board.NUMBER_OF_SQUARES; i++) {
            addView(new BoardSquareButton(getContext(), i));
        }
    }

    public static Board newBoardWithRandomPhrases(Context context, BufferedReader tsvReader) throws IOException {
        Board board = new Board(context);
        board.populateWithRandomPhrases(tsvReader);
        return board;
    }

    private void populateWithRandomPhrases(BufferedReader tsvReader) throws IOException {
        // TODO: we need to validate that there are at least 25 squares, possibly some other conditions
        // ignore the first line
        tsvReader.readLine();

        // TODO: method for picking random lines from a file could be improved, instead of loading entire file into memory should be able to use a sampling method
        List<String[]> rows = new ArrayList<>();
        String line;
        while ((line = tsvReader.readLine()) != null) {
            String[] row = line.split("\t");
            rows.add(row);
        }
        checkCategoryCount(rows);

        // first row in tsv is the free space row
        String[] freeSpaceData = rows.remove(0);
        Collections.shuffle(rows);
        rows = new ArrayList<>(rows.subList(0, NUMBER_OF_SQUARES-1));

        // if number of squares is odd, a center space exists
        if ((NUMBER_OF_SQUARES) % 2 != 0) {
            // add the free space row back in the center of the board
            rows.add((NUMBER_OF_SQUARES-1)/2, freeSpaceData);
        }

        for (int i = 0; i < NUMBER_OF_SQUARES; i++) {
            BoardSquareButton square = (BoardSquareButton) getChildAt(i);
            String[] row = rows.get(i);
            square.setText(row[0]);
//                if (row.length > 1) {
//                    square.setDescription(row[1]);
//                }
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int w = width / Board.NUMBER_OF_COLUMNS_AND_ROWS;
        int h = height / Board.NUMBER_OF_COLUMNS_AND_ROWS;
        for (int i = 0; i < Board.NUMBER_OF_SQUARES; ++i) {
            // tell the child exactly what size it needs to be
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO: entire method could probably be more efficient
        checkChildCount();
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

    // TODO: add unit test to check local categories in assets for the right amount of lines
    private void checkCategoryCount(List<?> rows) {
        if (BuildConfig.DEBUG && rows.size() < Board.NUMBER_OF_SQUARES) {
            throw new AssertionError("Expected category to have at least " + Board.NUMBER_OF_SQUARES + " squares");
        }
    }

    private void checkChildCount() {
        if (BuildConfig.DEBUG && getChildCount() != Board.NUMBER_OF_SQUARES) {
            throw new AssertionError("Expected the child count to be equal to the number of squares at all times.");
        }
    }
}

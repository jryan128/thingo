package io.jryan.thingo;

import android.content.Context;
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

    public Board(Context context, List<String[]> phraseData) {
        super(context);
        setId(R.id.boardView); // have to set an id, or we won't get saving
        createBoardSquares(phraseData);
    }

    public static Board createRandomBoardForCategory(Context context, BufferedReader tsvReader) {
        // TODO: we need to validate that there are at least 25 squares, possibly some other conditions
        try {
            // ignore the first line
            tsvReader.readLine();

            List<String[]> phraseData = new ArrayList<>();
            String line;
            while ((line = tsvReader.readLine()) != null) {
                String[] row = line.split("\t");
                phraseData.add(row);
            }

            String[] freeSpaceData = phraseData.remove(0);
            Collections.shuffle(phraseData);
            phraseData = new ArrayList<>(phraseData.subList(0, 24));
            phraseData.add(12, freeSpaceData);
            return new Board(context, phraseData);
        } catch (IOException e) {
            throw new RuntimeException("Could not make a board from category", e);
        } finally {
            if (tsvReader != null) {
                try {
                    tsvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createBoardSquares(List<String[]> allPhrases) {
        for (int i = 0; i < Board.NUMBER_OF_SQUARES; i++) {
            BoardSquareButton square = new BoardSquareButton(getContext());
            // FIXME, possible collisions?
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                square.setId(View.generateViewId());
//            } else {
            square.setId(i);
//            }
            addView(square);
            String[] phrase = allPhrases.get(i);
            square.setText(phrase[0]);
            if (phrase.length > 1) {
                square.setDescription(phrase[1]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

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

    private void checkChildCount() {
        if (BuildConfig.DEBUG && getChildCount() != Board.NUMBER_OF_SQUARES) {
            throw new AssertionError("Expected the child count to be equal to the number of squares at all times.");
        }
    }
}

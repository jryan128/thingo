package io.jryan.thingo;

import android.content.Context;
import android.os.Build;

import java.io.IOException;

public class Board {
    /** Must be greater than or equal to 0 */
    static final int NUMBER_OF_COLUMNS_AND_ROWS = 5;
    static {
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && NUMBER_OF_COLUMNS_AND_ROWS < 0) {
            throw new AssertionError("NUMBER_OF_COLUMNS_AND_ROWS must be >= 0");
        }
    }
    static final int NUMBER_OF_SQUARES = NUMBER_OF_COLUMNS_AND_ROWS * NUMBER_OF_COLUMNS_AND_ROWS;
    private final BoardView view;
    private final Context context;

    public Board(Context context) {
        this.context = context;
        view = new BoardView(context);
        try {
            new BoardController(context, view, new Patterns(context.getAssets()), new PointsKeeper()).setupBoardSquareButtonListeners();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < NUMBER_OF_SQUARES; i++) {
            BoardSquareButton square = (BoardSquareButton) view.getChildAt(i);
            square.setText("Someone Falls In Love");
            square.setDescription("Typical.");
        }
    }

    public BoardView getView() {
        return view;
    }
}

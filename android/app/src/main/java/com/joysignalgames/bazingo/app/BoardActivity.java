package com.joysignalgames.bazingo.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;

import java.io.IOException;
import java.util.List;

public class BoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        TestView testView = new TestView(this);
        setContentView(testView);
//        setContentView(R.layout.board);
//
//        if (savedInstanceState == null) {
//            String genre = getIntent().getStringExtra("genre");
//            try {
//                Board board = Board.loadRandomBoardFromCategory(genre, BoardActivity.this);
//                TableLayout tableLayout = (TableLayout) findViewById(R.id.board);
//                // FIXME: remove hard code 5
//                for (int i = 0; i < 5; i++) {
//                    TableRow row = (TableRow) tableLayout.getChildAt(i);
//                    // FIXME: remove hard code 5
//                    for (int j = 0; j < 5; j++) {
//                        Button square = (Button) row.getChildAt(j);
//                        square.setText(board.getPhrase((i * 5) + j));
//                    }
//                }
//            } catch (IOException e) {
//                // TODO: better error handling
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private static class TestView extends ViewGroup {

        private TestView(Context context) {
            super(context);
            init();
        }

        private TestView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private TestView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            setBackgroundColor(Color.BLACK);
            int color = 0xFF111111;
            for (int i=0; i<25; ++i) {
                View child = new View(getContext());
                child.setBackgroundColor(color - i * 20000);
                addView(child);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int childCount = getChildCount();
            int n = (int) Math.floor(Math.sqrt(childCount));
            int w = getMeasuredWidth() / n;
            int h = getMeasuredHeight() / n;

            for (int row=0; row < n; ++row) {
                for (int col=0; col < n; ++col) {
                    getChildAt((row * n) + col).layout(w * col, h * row, w * (col + 1), h * (row + 1));
                }
            }

        }
    }
}

package com.joysignalgames.bazingo.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.joysignalgames.bazingo.views.TestView;

import java.io.IOException;

public class BoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // dynamically populate the Board with squares
        TestView testView = new TestView(this);
        setContentView(testView);
        testView.setId(100);
        for (int i = 0; i < 25; i++) {
            BoardSquareButton square = new BoardSquareButton(this);
            square.setId(i);
            testView.addView(square);
        }

        // if we don't have a saved state, load a board into the grid
        if (savedInstanceState == null) {
            try {
                String genre = getIntent().getStringExtra("genre");
                Board board = Board.loadRandomBoardFromCategory(genre, BoardActivity.this);
                for (int i = 0; i < 25; i++) {
                    if (savedInstanceState == null) {
                        ((BoardSquareButton) testView.getChildAt(i)).setText(board.getPhrase(i));
                    }
                }
            } catch (IOException e) {
                // TODO: better error handling
                e.printStackTrace();
            }
        }
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

}

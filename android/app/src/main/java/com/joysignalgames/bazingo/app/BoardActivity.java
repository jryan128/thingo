package com.joysignalgames.bazingo.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.IOException;
import java.util.List;

public class BoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        if (savedInstanceState == null) {
            String genre = getIntent().getStringExtra("genre");
            try {
                Board board = Board.loadRandomBoardFromCategory(genre, BoardActivity.this);
                TableLayout tableLayout = (TableLayout) findViewById(R.id.board);
                // FIXME: remove hard code 5
                for (int i = 0; i < 5; i++) {
                    TableRow row = (TableRow) tableLayout.getChildAt(i);
                    // FIXME: remove hard code 5
                    for (int j = 0; j < 5; j++) {
                        Button square = (Button) row.getChildAt(j);
                        square.setText(board.getPhrase((i * 5) + j));
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

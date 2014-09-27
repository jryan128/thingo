package com.joysignalgames.bazingo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.IOException;

public class BoardActivity extends ActionBarActivity {
    private BoardView boardView;
    private Patterns patterns;
    private PointsKeeper pointsKeeper = new PointsKeeper();

    public static class PointsKeeper {
        public int points = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        try {
            setupWidgets();
            if (savedInstanceState == null) {
                populateBoardViewWithRandomPhrases();
            }
            // NOTE: see onPostCreate to see how and why controllers for BoardView are setup
        } catch (IOException e) {
            Log.e("BoardActivity", "Could not setup board activity.", e);
            // FIXME: fail some how?
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Setup the listeners after onRestoreInstanceState so the BoardSquareButtons don't
        // fire off listeners as they are being reloaded.
        // ASSERT: All button listeners have gone off after a possible re-load
        // during onRestoreInstanceState AND the user cannot interact with the board yet.
        // NOTE: It probably would be more clear and explicit to just turn off saveEnabled on BoardView and
        // handle all of the saving/loading manually. But I'm lazy.
        new BoardController(this, boardView, patterns, pointsKeeper).setupBoardSquareButtonListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("patterns", patterns);
        outState.putInt("points", pointsKeeper.points);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        patterns = savedInstanceState.getParcelable("patterns");
        pointsKeeper.points = savedInstanceState.getInt("points");
    }

    private void setupWidgets() throws IOException {
        patterns = new Patterns(getAssets());
        boardView = new BoardView(this);
        setContentView(boardView);
    }

    private void populateBoardViewWithRandomPhrases() {
        String genre = getIntent().getStringExtra("genre");
        try {
            Board board = Board.loadRandomBoardFromCategory(genre, BoardActivity.this);
            for (int i = 0; i < 25; i++) {
                BoardSquareButton square = (BoardSquareButton) boardView.getChildAt(i);
                square.setText(board.getPhrase(i));
                square.setDescription(board.getDescription(i));
            }
        } catch (IOException e) {
            Log.e("BoardActivity", "Could not load the genre (" + genre + ").", e);
            // FIXME: popup an error before going back
            Intent intent = new Intent(getBaseContext(), GenreSelectionActivity.class);
            startActivity(intent);
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

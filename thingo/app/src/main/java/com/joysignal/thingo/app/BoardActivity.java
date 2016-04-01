package com.joysignal.thingo.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.joysignal.thingo.app.board.*;

import java.io.IOException;

public class BoardActivity extends Activity {
    private BoardView boardView;
    private Patterns patterns;
    private final PointsKeeper pointsKeeper = new PointsKeeper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        enterImmseriveMode();

        try {
            setupWidgets();
            if (savedInstanceState == null) {
                populateBoardViewWithRandomPhrases();
            }
            // NOTE: see onPostCreate to see how and why controllers for BoardView are setup
        } catch (IOException e) {
            Log.e("BoardActivity", "Could not setup board activity.", e);
            // FIXME: popup an error before going back
            Intent intent = new Intent(getBaseContext(), BoardCategoriesActivity.class);
            startActivity(intent);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("patterns", patterns);
        outState.putInt("points", pointsKeeper.points);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
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
            BoardModel board = BoardModel.loadRandomBoardFromCategory(genre, BoardActivity.this);
            for (int i = 0; i < 25; i++) {
                BoardSquareButton square = (BoardSquareButton) boardView.getChildAt(i);
                square.setText(board.getPhrase(i));
                square.setDescription(board.getDescription(i));
            }
        } catch (IOException e) {
            Log.e("BoardActivity", "Could not load the genre (" + genre + ").", e);
            // FIXME: popup an error before going back
            Intent intent = new Intent(getBaseContext(), BoardCategoriesActivity.class);
            startActivity(intent);
        }
    }

    private void enterImmersiveMode() {
        // FIXME: Taken from Android samples, include proper license and whatever. (Apache 2.0)
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}

package io.jryan.thingo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        startImmersiveMode();
        RelativeLayout boardContainer = (RelativeLayout) findViewById(R.id.board);
        Board board = makeBoard(savedInstanceState);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        boardContainer.addView(board, params);
    }

    private Board makeBoard(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // only populate phrases on initial onCreate
            // TODO: don't hard code RomCom
            BufferedReader romComTsv = new LocalCategories(getAssets()).makeReaderForCategoryFile(getAssets(), "Romantic Comedy");
            return Board.newBoardWithRandomPhrases(this, romComTsv);
        } else {
            return new Board(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startImmersiveMode();
    }

    private void startImmersiveMode() {
        RelativeLayout boardContainer = (RelativeLayout) findViewById(R.id.board);
        boardContainer.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}

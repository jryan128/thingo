package io.jryan.thingo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.BufferedReader;

public class BoardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        RelativeLayout boardContainer = (RelativeLayout) findViewById(R.id.board);
        startImmersiveMode();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        BufferedReader romComTsv = new LocalCategories(getAssets()).makeReaderForCategoryFile(getAssets(), "Romantic Comedy");
        boardContainer.addView(Board.createRandomBoardForCategory(this, romComTsv), params);
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

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
        Board board = new Board(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        boardContainer.addView(board, params);

        if (savedInstanceState == null) {
            BufferedReader romComTsv = new LocalCategories(getAssets()).makeReaderForCategoryFile(getAssets(), "Romantic Comedy");
            populateWithRandomPhrases(romComTsv, board);
        }
    }

    public static void populateWithRandomPhrases(BufferedReader tsvReader, Board board) {
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
            for (int i = 0; i < 25; i++) {
                BoardSquareButton square = (BoardSquareButton) board.getChildAt(i);
                String[] data = phraseData.get(i);
                square.setText(data[0]);
//                if (data.length > 1) {
//                    square.setDescription(data[1]);
//                }
            }
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

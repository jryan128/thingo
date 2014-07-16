package com.joysignalgames.bazingo.app;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private final List<String[]> phraseData;

    public static Board loadRandomBoardFromCategory(String category, Activity activity) throws IOException {
        // TODO: we need to validate that there are at least 25 squares, possibly some other conditions
        BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open("phrases/" + category)));

        // ignore the first line
        reader.readLine();

        List<String[]> phraseData = new ArrayList<String[]>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] row = line.split("\t");
            phraseData.add(row);
        }

        String[] freeSpaceData = phraseData.remove(0);
        Collections.shuffle(phraseData);
        phraseData = new ArrayList<String[]>(phraseData.subList(0, 24));
        phraseData.add(12, freeSpaceData);
        return new Board(phraseData);
    }

    private Board(List<String[]> phraseData) {
        this.phraseData = phraseData;
    }

    public String getPhrase(int position) {
        return phraseData.get(position)[0];
    }
}

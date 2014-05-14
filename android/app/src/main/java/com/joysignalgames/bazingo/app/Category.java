package com.joysignalgames.bazingo.app;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Category {
    private final List<String[]> phraseData;
    private final String[] freeSpaceData;

    private Category(List<String[]> phraseData) {
        this.phraseData = new ArrayList<String[]>(phraseData.subList(0, 24));
        this.freeSpaceData = phraseData.get(25);
    }

    public static Category loadCategory(String category, Activity activity) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open("phrases/" + category)));

        List<String[]> phraseData = new ArrayList<String[]>();
        //noinspection UnusedAssignment
        String line = reader.readLine(); //right now just the column headers

        while ((line = reader.readLine()) != null) {
            String[] row = line.split("\t");
            phraseData.add(row);
        }

        // TODO: we need to validate that there are at least 25 squares, possibly some other conditions

        // choose random phrases to fill up 25 square board
        // probably less optimal than generating random indices (without repeats) but for now, less code
        Collections.shuffle(phraseData);
        return new Category(phraseData);
    }

    public List<String[]> getPhrases() {
        return Collections.unmodifiableList(phraseData);
    }

    public String getPhrase(int position) {
        return phraseData.get(position)[0];
    }

    public String getPhraseDescription(int position) {
        return phraseData.get(position)[1];
    }

    public String getFreeSpace() {
        return freeSpaceData[0];
    }

    public String getFreeSpaceDescription() {
        return freeSpaceData[1];
    }
}

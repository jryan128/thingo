package com.joysignal.thingo.app.board;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardModel {
    public static final String TAG = BoardModel.class.getSimpleName();
    private final List<String[]> phraseData;

    public static BoardModel loadRandomBoardFromCategory(String category, Activity activity) throws IOException {
        // TODO: Need to validate that there are at least 25 squares, possibly some other conditions
        BufferedReader reader = new Categories(activity).makeReaderForCategory(category);
        try {
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
            return new BoardModel(phraseData);
        } finally {
            close(reader);
        }
    }

    private BoardModel(List<String[]> phraseData) {
        this.phraseData = phraseData;
    }

    public String getPhrase(int position) {
        return phraseData.get(position)[0];
    }

    public String getDescription(int position) {
        String[] data = phraseData.get(position);
        if (data.length >= 2) {
            return data[1];
        } else {
            return "No description.";
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.w(TAG, "Could not close closable " + closeable, e);
            }
        }
    }
}

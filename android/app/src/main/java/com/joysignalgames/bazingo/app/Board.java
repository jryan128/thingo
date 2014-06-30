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

public class Board implements Parcelable {
    private List<String[]> phraseData;

    private Board(List<String[]> phraseData) {
        String[] freeSpaceData = phraseData.get(0);
        Collections.shuffle(phraseData);
        this.phraseData = new ArrayList<String[]>(phraseData.subList(0, 24));
        this.phraseData.add(12, freeSpaceData);
    }

    private Board(Parcel parcel) {
        this.phraseData = new ArrayList<String[]>(5);

        String[] next;
        while ((next = parcel.createStringArray()) != null) {
            phraseData.add(next);
        }
    }

    public static Board loadRandomBoardFromCategory(String category, Activity activity) throws IOException {
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
        return new Board(phraseData);
    }

    public List<String[]> getPhrases() {
        return Collections.unmodifiableList(phraseData);
    }

    public void setPhrases(List<String[]> phrases) {
        this.phraseData = phrases;
    }

    public String getPhrase(int position) {
        return phraseData.get(position)[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (String[] row : phraseData) {
            dest.writeStringArray(row);
        }
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel source) {
            return new Board(source);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };
}

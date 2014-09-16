package com.joysignalgames.bazingo;

import android.content.res.AssetManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Patterns implements Parcelable {
    public static class Pattern implements Parcelable {
        public final String name;
        public final int points;
        private int count = 0;
        public final List<Integer> squares = new ArrayList<Integer>();

        private Pattern(String name, int points) {
            this.name = name;
            this.points = points;
        }

        private Pattern(Parcel parcel) {
            this.name = parcel.readString();
            this.points = parcel.readInt();
            this.count = parcel.readInt();
            parcel.readList(squares, null);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(points);
            dest.writeInt(count);
            dest.writeList(squares);
        }

        @Override
        public String toString() {
            return "Pattern{" +
                    "name='" + name + '\'' +
                    ", points=" + points +
                    ", count=" + count +
                    ", squares=" + squares +
                    '}';
        }

        public static final Parcelable.Creator<Pattern> CREATOR
                = new Parcelable.Creator<Pattern>() {
            public Pattern createFromParcel(Parcel in) {
                return new Pattern(in);
            }

            public Pattern[] newArray(int size) {
                return new Pattern[size];
            }
        };
    }

    private static final int TOTAL_SQUARES = 25;
    private ArrayList<Set<Pattern>> patternBuckets = new ArrayList<Set<Pattern>>(TOTAL_SQUARES);

    public Patterns(AssetManager assets) throws IOException {
        this.patternBuckets = makePatternBuckets(assets);
    }

    public Set<Pattern> squareSelected(int i) {
        Set<Pattern> patterns = patternBuckets.get(i);
        Set<Pattern> completedPatterns = new HashSet<Pattern>();
        for (Pattern pattern : patterns) {
            // FIXME: is this even helpful?
            if (pattern.count <= 0) {
                pattern.count = 0;
            } else {
                pattern.count -= 1;
            }

            if (pattern.count == 0) {
                completedPatterns.add(pattern);
            }
        }
        return completedPatterns;
    }

    public Set<Pattern> squareUnselected(int i) {
        Set<Pattern> patterns = patternBuckets.get(i);
        Set<Pattern> nowUncompletedPatterns = new HashSet<Pattern>();
        for (Pattern pattern : patterns) {
            // FIXME: should we make sure we're not going over the max somehow?
            if (pattern.count == 0) {
                nowUncompletedPatterns.add(pattern);
            }
            pattern.count += 1;
        }
        return nowUncompletedPatterns;
    }

    private static ArrayList<Set<Pattern>> makePatternBuckets(AssetManager assets) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assets.open("patterns.tsv")));
            ArrayList<Set<Pattern>> patternBuckets = createSquaresList();
            parsePatternFile(patternBuckets, br);
            return patternBuckets;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private static ArrayList<Set<Pattern>> createSquaresList() {
        ArrayList<Set<Pattern>> squares = new ArrayList<Set<Pattern>>(TOTAL_SQUARES);
        for (int i = 0; i < TOTAL_SQUARES; i++) {
            squares.add(new HashSet<Pattern>());
        }
        return squares;
    }

    private static void parsePatternFile(ArrayList<Set<Pattern>> patternBuckets, BufferedReader br) throws IOException {
        // FIXME: Every time this is re-created (via BoardActivity) we reload from the tsv file.
        // Instead, we should cache this information
        String line;
        while ((line = br.readLine()) != null) {
            // ASSERT: File is assumed to be well formed.
            // TODO: Is this a safe assumption?
            final String[] split = line.split("\\t");
            Pattern pattern = new Pattern(split[0], Integer.parseInt(split[2]));
            String[] squaresArray = split[1].split(" ");
            for (String squareStr : squaresArray) {
                pattern.count += 1;
                int squareId = Integer.parseInt(squareStr);
                patternBuckets.get(squareId).add(pattern);
                pattern.squares.add(squareId);
            }
        }
    }

    @Override
    public String toString() {
        return "Patterns{" +
                "patternBuckets=" + patternBuckets +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (Set<Pattern> patternBucket : patternBuckets) {
            dest.writeTypedList(new ArrayList<Pattern>(patternBucket));
        }
    }

    private Patterns(Parcel in) {
        this.patternBuckets = new ArrayList<Set<Pattern>>(TOTAL_SQUARES);
        for (int i = 0; i < TOTAL_SQUARES; i++) {
            ArrayList<Pattern> list = new ArrayList<Pattern>();
            in.readTypedList(list, Pattern.CREATOR);
            patternBuckets.get(i).addAll(list);
        }
    }

    public static final Parcelable.Creator<Patterns> CREATOR
            = new Parcelable.Creator<Patterns>() {
        public Patterns createFromParcel(Parcel in) {
            return new Patterns(in);
        }

        public Patterns[] newArray(int size) {
            return new Patterns[size];
        }
    };
}

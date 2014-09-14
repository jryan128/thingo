package com.joysignalgames.bazingo;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Patterns {
    public static class Pattern {
        public final String name;
        public final int points;
        private int count = 0;

        private Pattern(String name, int points) {
            this.name = name;
            this.points = points;
        }

        @Override
        public String toString() {
            return "Pattern{" +
                    "name='" + name + '\'' +
                    ", points=" + points +
                    ", count=" + count +
                    '}';
        }
    }

    private static final int TOTAL_SQUARES = 24;
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
        String line;
        while ((line = br.readLine()) != null) {
            // ASSERT: file is assumed to be well formed
            // TODO: is this a safe assumption?
            final String[] split = line.split("\\t");
            Pattern pattern = new Pattern(split[0], Integer.parseInt(split[2]));
            String[] squaresArray = split[1].split(" ");
            for (String squareStr : squaresArray) {
                pattern.count += 1;
                patternBuckets.get(Integer.parseInt(squareStr)).add(pattern);
            }
        }
    }

    @Override
    public String toString() {
        return "Patterns{" +
                "patternBuckets=" + patternBuckets +
                '}';
    }
}

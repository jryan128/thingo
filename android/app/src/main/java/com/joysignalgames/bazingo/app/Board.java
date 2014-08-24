package com.joysignalgames.bazingo.app;

import android.app.Activity;
import com.joysignalgames.bazingo.Genres;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private final List<String[]> phraseData;

    public static Board loadRandomBoardFromCategory(String category, Activity activity) throws IOException {
        // TODO: we need to validate that there are at least 25 squares, possibly some other conditions
        BufferedReader reader = Genres.INSTANCE.getGenrePhrasesFile(activity.getAssets(), category);

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

    // FIXME: javadocs, docs
    // FIXME: make parcelable
    private static class PatternMatcher {
        private final Square[] squares = new Square[0];

        private PatternMatcher() {
            // FIXME: parse JSON file, and create squares
            // this.squares = ?
        }

        private static class Square {
            private Set<Pattern> patterns = new HashSet<Pattern>();

            private Square(Set<Pattern> patterns) {
                this.patterns = patterns;
            }

            private void select() {
                Set<Pattern> isCompleted = new HashSet<Pattern>();
                for (Pattern pattern : patterns) {
                    pattern.select();
                    if (pattern.isCompleted()) {
                        isCompleted.add(pattern);
                    }
                }
                handleCompletedPatterns(isCompleted);
            }

            private void handleCompletedPatterns(Set<Pattern> isCompleted) {
                // FIXME: popup a gui with the completed patterns dialog
            }

            private void deselect() {
                for (Pattern pattern : patterns) {
                    pattern.deselect();
                }
            }
        }

        private static class Pattern {
            private final String name;
            private final int neededSelectedCount;
            private int currentSelectedCount;

            private Pattern(String name, int neededSelectedCount) {
                this.name = name;
                this.neededSelectedCount = neededSelectedCount;
                assertAssumptions();
            }

            public void select() {
                currentSelectedCount += 1;
                assertAssumptions();
            }

            public void deselect() {
                currentSelectedCount -= 1;
                assertAssumptions();
            }

            public boolean isCompleted() {
                assertAssumptions();
                return currentSelectedCount == neededSelectedCount;
            }

            private void assertAssumptions() {
                // FIXME: correct way to assert
                assert currentSelectedCount <= neededSelectedCount;
                assert currentSelectedCount >= 0;
            }
        }
    }
}

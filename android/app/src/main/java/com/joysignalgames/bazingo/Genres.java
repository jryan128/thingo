package com.joysignalgames.bazingo;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Access genres and genre files.
 *
 * Since it's static across the entire application, and I can't imagine a
 * case where the app would want different genre lists in different parts of the app; I'm making this
 * an evil singleton (gasp).
 */
public enum Genres {
    INSTANCE; // singleton instance

    public static final String GENRES_DIR = "genres";
    private static final String GENRES_LOG_TAG = "Genres";

    public Set<String> getGenreNames(AssetManager assets) throws IOException {
        // FIXME: create test if the phrases have good file names and no dupes
        String[] categoryTsvFiles = assets.list(GENRES_DIR);

        Set<String> categoryList = new HashSet<String>();
        for (String fileName : categoryTsvFiles) {
            // FIXME: write a intg test to test that there are no files not ending in .tsv
            if (!fileName.endsWith(".tsv")) {
                Log.w(GENRES_LOG_TAG, "Unexpected genre file in " + GENRES_DIR + ": [" + fileName + "]. Should end in \".tsv\".");
                continue;
            }

            try {
                String genreName = fileName.substring(0, fileName.lastIndexOf('.'));
                if(!categoryList.add(genreName)) {
                    Log.w(GENRES_LOG_TAG, "Duplicate genre found: [name:" + genreName + ", fileName: " + fileName + ". Ignoring this genre, and keeping old one.");
                }
            } catch (IndexOutOfBoundsException ex) {
                Log.e(GENRES_LOG_TAG, "Unable to parse expected genre file name: " + fileName + ", expecting a file that ends in \".tsv\"", ex);
            }
        }
        return categoryList;
    }

    // FIXME: write tests to load each tsv file and make sure there are no errors
    public BufferedReader getGenrePhrasesFile(AssetManager assets, String genreName) throws IOException {
        return new BufferedReader(new InputStreamReader(assets.open(GENRES_DIR + File.separator + genreName + ".tsv")));
    }
}

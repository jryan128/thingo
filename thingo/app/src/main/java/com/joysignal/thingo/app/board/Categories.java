package com.joysignal.thingo.app.board;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A singleton to access genre data (.tsv) files.</p>
 *
 * <p>Why a singleton?
 * <ol>
 *  <li>Genre data is static across the entire application and I can't imagine a
 *      case in which the app would want different genre lists in different parts of the app.</li>
 *  <li>It's also hard to move objects between Android <code>Activity</code> objects.</li>
 * </ol>
 */
// FIXME: Rename to Categories
enum Categories {
    INSTANCE; // singleton instance

    private static final String DIR = "categories";
    private static final String LOG_TAG = "Categories";

    public Set<String> getCategoryNames(AssetManager assets) throws IOException {
        // FIXME: create test if the phrases have good file names and no dupes
        String[] categoryTsvFiles = assets.list(DIR);

        Set<String> categoryList = new HashSet<String>();
        for (String fileName : categoryTsvFiles) {
            // FIXME: write a intg test to test that there are no files not ending in .tsv
            if (!fileName.endsWith(".tsv")) {
                Log.w(LOG_TAG, "Unexpected genre file in " + DIR + ": [" + fileName + "]. Should end in \".tsv\".");
                continue;
            }

            try {
                String genreName = fileName.substring(0, fileName.lastIndexOf('.'));
                if(!categoryList.add(genreName)) {
                    Log.w(LOG_TAG, "Duplicate genre found: [name:" + genreName + ", fileName: " + fileName + ". Ignoring this genre, and keeping old one.");
                }
            } catch (IndexOutOfBoundsException ex) {
                Log.e(LOG_TAG, "Unable to parse expected genre file name: " + fileName + ", expecting a file that ends in \".tsv\"", ex);
            }
        }
        return categoryList;
    }

    // FIXME: write tests to load each tsv file and make sure there are no errors
    public BufferedReader getCategoryPhraseFile(AssetManager assets, String genreName) throws IOException {
        return new BufferedReader(new InputStreamReader(assets.open(DIR + File.separator + genreName + ".tsv")));
    }
}

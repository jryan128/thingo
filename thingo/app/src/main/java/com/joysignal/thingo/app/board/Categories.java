package com.joysignal.thingo.app.board;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Categories {
    private static final String DIR = "categories";
    private static final String LOG_TAG = "Categories";

    private Context context;

    public Categories(Context context) {
        this.context = context;
    }

    public Set<String> getCategoryNames() {
        // FIXME: create test if the phrases have good file names and no dupes
        // TODO: Probably could cache.
        try {
            String[] categoryTsvFiles = context.getAssets().list(DIR);
            Set<String> categoryList = new HashSet<String>();
            for (String fileName : categoryTsvFiles) {
                // FIXME: write a intg test to test that there are no files not ending in .tsv
                if (!fileName.endsWith(".tsv")) {
                    Log.w(LOG_TAG, "Unexpected category file in " + DIR + ": [" + fileName + "]. Should end in \".tsv\".");
                    continue;
                }

                try {
                    String category = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (!categoryList.add(category)) {
                        Log.w(LOG_TAG, "Duplicate category found: [name:" + category + ", fileName: " + fileName + ". Ignoring this category, and keeping old one.");
                    }
                } catch (IndexOutOfBoundsException ex) {
                    Log.e(LOG_TAG, "Unable to parse category file name: " + fileName + ", expecting a filename that ends in \".tsv\"", ex);
                }
            }
            return categoryList;
        } catch (IOException e) {
            throw new RuntimeException("Could not list files in categories folder.", e);
        }
    }

    // FIXME: write tests to load each tsv file and make sure there are no errors
    BufferedReader makeReaderForCategory(String categoryName) {
        final String fileName = DIR + File.separator + categoryName + ".tsv";
        try {
            return new BufferedReader(new InputStreamReader(context.getAssets().open(fileName, AssetManager.ACCESS_BUFFER)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find category file " + fileName, e);
        } catch (IOException e) {
            throw new RuntimeException("Could not load category file " + fileName, e);
        }
    }
}

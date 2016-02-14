package com.joysignal.thingo.app.board;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

class Categories {
    private static final AtomicBoolean FIRST_INSTANCE = new AtomicBoolean(true);
    private static final String DIR = "categories";
    private static final String LOG_TAG = "Categories";

    private Context context;

    Categories(Context context) {
        if (FIRST_INSTANCE.compareAndSet(true, false)) {
            syncBuiltInCategories(context);
        }
        this.context = context;
    }

    private static void syncBuiltInCategories(Context context) {
        String[] files = context.fileList();
        for (String file : files) {
            System.out.println(file);
        }
    }

    Set<String> getCategoryNames() {
        // FIXME: create test if the phrases have good file names and no dupes
        // TODO: Probably could cache.
        String[] categoryTsvFiles = context.fileList();
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
    }

    // FIXME: write tests to load each tsv file and make sure there are no errors
    BufferedReader makeReaderForCategory(String categoryName) {
        final String fileName = DIR + File.separator + categoryName + ".tsv";
        try {
            return new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find category file " + fileName, e);
        }
    }
}

package io.jryan.thingo;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A local to access board categories</p>
 */
public class LocalCategories {
    private static final String TAG = LocalCategories.class.getSimpleName();
    private static final String CATEGORIES_FOLDER = "categories";

    private final AssetManager assets;

    public LocalCategories(AssetManager assets) {
        this.assets = assets;
    }

    public Set<String> list() {
        // FIXME: create test if the phrases have good file names and no dupes
        String[] categoryTsvFiles;
        try {
            categoryTsvFiles = assets.list(CATEGORIES_FOLDER);
        } catch (IOException e) {
            throw new RuntimeException("Could not load find categories in asset folder", e);
        }

        Set<String> categoryList = new HashSet<>();
        for (String fileName : categoryTsvFiles) {
            // FIXME: write a intg test to test that there are no files not ending in .tsv
            if (!fileName.endsWith(".tsv")) {
                Log.w(TAG, "Unexpected category file in " + CATEGORIES_FOLDER + ": [" + fileName + "]. Should end in \".tsv\".");
                continue;
            }

            try {
                String categoryName = fileName.substring(0, fileName.lastIndexOf('.'));
                if (!categoryList.add(categoryName)) {
                    Log.w(TAG, "Duplicate category found: [name:" + categoryName + ", fileName: " + fileName + ". Ignoring this category, and keeping old one.");
                }
            } catch (IndexOutOfBoundsException ex) {
                Log.e(TAG, "Unable to parse expected category file name: " + fileName + ", expecting a file that ends in \".tsv\"", ex);
            }
        }
        return categoryList;
    }

    // FIXME: write tests to load each tsv file and make sure there are no errors
    public BufferedReader makeReaderForCategoryFile(String categoryName) throws IOException {
        InputStream tsv = assets.open(CATEGORIES_FOLDER + File.separator + categoryName + ".tsv");
        return new BufferedReader(new InputStreamReader(tsv));
    }
}

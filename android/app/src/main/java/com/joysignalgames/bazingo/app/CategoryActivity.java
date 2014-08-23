package com.joysignalgames.bazingo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        try {
            List<String> phrases = getPhrasesList();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, phrases);

            EditText editText = (EditText) findViewById(R.id.search_genres);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    adapter.getFilter().filter(arg0);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // empty
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // empty
                }
            });

            ListView listView = (ListView) findViewById(R.id.genres);
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    Intent intent = new Intent(getBaseContext(), BoardActivity.class);
                    intent.putExtra("genre", ((TextView) view).getText().toString());
                    startActivity(intent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // getting the array adapter this way since the phrases are all in .tsv files in our repository so
    // it will be easier for now to just copy those files and parse them here as opposed to making separate .xml files
    List<String> getPhrasesList() throws IOException {

        // FIXME: move this phrase getting logic into own class
        // create an intg test that tests if the phrases have good file names
        String[] categoryTsvFiles = getAssets().list("phrases");

        // FIXME: make a set?
        List<String> categoryList = new ArrayList<String>();
        for (String fileName : categoryTsvFiles) {
            try {
                categoryList.add(fileName.substring(0, fileName.lastIndexOf('.')));
            } catch (IndexOutOfBoundsException ex) {
                // log bad file properly
                ex.printStackTrace();
            }
        }
        return categoryList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}

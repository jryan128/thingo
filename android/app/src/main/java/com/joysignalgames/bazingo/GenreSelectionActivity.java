package com.joysignalgames.bazingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.joysignalgames.bazingo.BoardActivity;
import com.joysignalgames.bazingo.Genres;
import com.joysignalgames.bazingo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreSelectionActivity extends ActionBarActivity {

    public static final String CATEGORY_ACTIVITY_LOG_TAG = "CategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_page);

        try {
            setupWidgets();
        } catch (IOException e) {
            // FIXME: pop up error message
            String msg = "Could not load the genre list.";
            Log.e(CATEGORY_ACTIVITY_LOG_TAG, msg + ". Quitting application.", e);
            throw new RuntimeException(msg, e);
        }
    }

    private void setupWidgets() throws IOException {
        List<String> genres = new ArrayList<String>(Genres.INSTANCE.getGenreNames(getAssets()));
        final ArrayAdapter<String> genreList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, genres);

        EditText editText = (EditText) findViewById(R.id.search_genres);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                genreList.getFilter().filter(arg0);
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
        listView.setAdapter(genreList);
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

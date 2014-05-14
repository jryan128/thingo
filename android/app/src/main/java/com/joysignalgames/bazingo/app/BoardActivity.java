package com.joysignalgames.bazingo.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class BoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        try {
            // probably check to make sure there is an extra called genre
            String genre = getIntent().getStringExtra("genre");
            Category category = Category.loadCategory(genre, this);

            GridView boardView = (GridView) findViewById(R.id.board);
            boardView.setAdapter(new BoardAdapter(category));
        } catch (IOException e) {
            // TODO: better error handling
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_board, menu);
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

    public class BoardAdapter extends BaseAdapter {

        private final Category category;

        private BoardAdapter(Category category) {
            this.category = category;
        }

        @Override
        public int getCount() {
            return category.getPhrases().size();
        }

        @Override
        public Object getItem(int position) {
            return category.getPhrase(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.board_square, null);
                ((BoardSquareButton) convertView).setText((String) getItem(position));
            }
            return convertView;
        }
    }
}

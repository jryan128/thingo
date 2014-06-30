package com.joysignalgames.bazingo.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.io.IOException;

public class BoardActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        setupBoardGridView(savedInstanceState);
    }

    private void setupBoardGridView(Bundle savedInstanceState) {
        try {
            GridView boardView = (GridView) findViewById(R.id.board);
            boardView.setAdapter(new BoardAdapter(savedInstanceState));
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GridView boardView = (GridView) findViewById(R.id.board);
        outState.putParcelable("board", ((BoardAdapter) boardView.getAdapter()).board);
    }

    public class BoardAdapter extends BaseAdapter {

        private final Board board;

        private BoardAdapter(Bundle bundle) throws IOException {
            this.board = setupBoard(bundle);
        }

        private Board setupBoard(Bundle bundle) throws IOException {
            // if board was saved, get that board, otherwise create new random board
            if (bundle != null) {
                Parcelable savedBoard = bundle.getParcelable("board");
                if (savedBoard != null) {
                    return (Board) savedBoard;
                }
            }
            String genre = getIntent().getStringExtra("genre");
            return Board.loadRandomBoardFromCategory(genre, BoardActivity.this);
        }

        @Override
        public int getCount() {
            return board.getPhrases().size();
        }

        @Override
        public Object getItem(int position) {
            return board.getPhrase(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.board_square, null);
            }
            ((BoardSquareButton) convertView).setText((String) getItem(position));
            return convertView;
        }
    }
}

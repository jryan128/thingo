package com.joysignalgames.bazingo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CustomBoardListActivity extends ActionBarActivity {
    // TODO: Break out into own class that can get and add new boards via web service
    private final List<String> customBoards = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_board_page);
        setupListView();
    }

    private void setupListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(createListAdapter());
    }

    private ArrayAdapter<String> createListAdapter() {
        // TODO: Load from web service, or local...
        return new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, customBoards);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custom_board_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new_board) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Board Name");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newBoardName = input.getText().toString();
                    customBoards.add(newBoardName);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}

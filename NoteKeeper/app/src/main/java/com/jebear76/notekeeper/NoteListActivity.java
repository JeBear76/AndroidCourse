package com.jebear76.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> _noteInfoArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);

                startActivity(intent);
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        _noteInfoArrayAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        final ListView listNotes = (ListView) findViewById(R.id.list_notes);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        _noteInfoArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, notes);

        listNotes.setAdapter(_noteInfoArrayAdapter);

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //NoteInfo noteInfo = (NoteInfo) listNotes.getItemAtPosition(position);
                //Intent intent = new Intent(getString(R.string.view_note_into_action));
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                //intent.putExtra(NoteActivity.NOTE_INFO, noteInfo);
                intent.putExtra(NoteActivity.NOTE_POSITION, position);
                startActivity(intent);

            }
        });
    }
}

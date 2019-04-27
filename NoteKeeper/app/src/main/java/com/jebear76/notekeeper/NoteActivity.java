package com.jebear76.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_INFO = "com.jebear76.notekeeper.NOTE_INFO";
    public static final String NOTE_POSITION = "com.jebear76.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo noteInfo;
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textTitle;
    private EditText textBody;
    private int position;
    private boolean isCancelling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();

        textTitle = findViewById(R.id.editText_note_title);
        textBody = findViewById(R.id.editText_note_body);

        if (!isNewNote) {
            textTitle.setText(noteInfo.getTitle());
            textBody.setText(noteInfo.getBody());

            int courseIndex = courses.indexOf(noteInfo.getCourse());
            spinnerCourses.setSelection(courseIndex);
        }
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //noteInfo = intent.getParcelableExtra(NoteActivity.NOTE_INFO);
        position = intent.getIntExtra(NoteActivity.NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = position == POSITION_NOT_SET;
        if(isNewNote){
            position = DataManager.getInstance().createNewNote();
        }
        noteInfo = DataManager.getInstance().getNotes().get(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_cancel) {
            isCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCancelling){
            if(isNewNote){
                DataManager.getInstance().removeNote(position);
            }
            return;
        }
        saveNote();

    }

    private void saveNote() {
        noteInfo.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        noteInfo.setTitle(textTitle.getText().toString());
        noteInfo.setText(textBody.getText().toString());
    }
}

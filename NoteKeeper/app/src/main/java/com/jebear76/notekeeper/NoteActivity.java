package com.jebear76.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
    private NoteInfo originalNoteInfo;

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
        if(savedInstanceState == null){
            saveOriginalNote();
        }else{
            restoreOriginalNoteInfo(savedInstanceState);
        }

        textTitle = findViewById(R.id.editText_note_title);
        textBody = findViewById(R.id.editText_note_body);

        if (!isNewNote) {
            textTitle.setText(noteInfo.getTitle());
            textBody.setText(noteInfo.getBody());

            int courseIndex = courses.indexOf(noteInfo.getCourse());
            spinnerCourses.setSelection(courseIndex);
        }
    }

    private void restoreOriginalNoteInfo(Bundle savedInstanceState) {
        originalNoteInfo = savedInstanceState.getParcelable(NOTE_INFO);
    }

    private void saveOriginalNote() {
        if (isNewNote)
            return;
        originalNoteInfo = new NoteInfo(noteInfo);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //noteInfo = intent.getParcelableExtra(NoteActivity.NOTE_INFO);
        position = intent.getIntExtra(NoteActivity.NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = position == POSITION_NOT_SET;
        if (isNewNote) {
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

        if (id == R.id.menu_action_send_mail) {
            isCancelling = true;

            sendEmail();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_cancel) {
            isCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        String subject = "Check this out \"" + ((CourseInfo)spinnerCourses.getSelectedItem()).getTitle() + "\"" ;
        String body = textTitle.getText().toString() + "\n" + textBody.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelling) {
            if (isNewNote) {
                DataManager.getInstance().removeNote(position);
            } else {
                noteInfo = originalNoteInfo;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NOTE_INFO, originalNoteInfo);
    }
}

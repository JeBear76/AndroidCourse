package com.jebear76.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_INFO = "com.jebear76.notekeeper.NOTE_INFO";
    public static final String NOTE_ID = "com.jebear76.notekeeper.NOTE_ID";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo _noteInfo;
    private boolean _isNewNote;
    private Spinner _spinnerCourses;
    private EditText _textTitle;
    private EditText _textBody;
    private int _currentID;
    private boolean _isCancelling = true;
    private NoteInfo _originalNoteInfo;
    private List<CourseInfo> _courses;
    private NoteKeeperOpenHelper _noteKeeperOpenHelper;
    private Cursor _noteCursor;

    @Override
    protected void onDestroy() {
        _noteKeeperOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _noteKeeperOpenHelper = new NoteKeeperOpenHelper(this);

        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        _courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, _courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();

        _textTitle = findViewById(R.id.editText_note_title);
        _textBody = findViewById(R.id.editText_note_body);

        displayNote();
    }

    private void displayNote() {
        if (!_isNewNote && _noteCursor.moveToNext()) {
            _textTitle.setText(_noteCursor.getString(_noteCursor.getColumnIndex(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE)));
            _textBody.setText(_noteCursor.getString(_noteCursor.getColumnIndex(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TEXT)));

            CourseInfo courseInfo = DataManager.getInstance().getCourse(_noteCursor.getString(_noteCursor.getColumnIndex(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID)));
            int courseIndex = _courses.indexOf(courseInfo);
            _spinnerCourses.setSelection(courseIndex);
        }
    }

    private void restoreOriginalNoteInfo(Bundle savedInstanceState) {
        _originalNoteInfo = savedInstanceState.getParcelable(NOTE_INFO);
    }

    private void saveOriginalNote() {
        if (_isNewNote)
            return;
        _originalNoteInfo = new NoteInfo(_noteInfo);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        _currentID = intent.getIntExtra(NoteActivity.NOTE_ID, POSITION_NOT_SET);
        Log.i(TAG, "_currentID:" + _currentID);
        _isNewNote = _currentID == POSITION_NOT_SET;
        Log.i(TAG, "_isNewNote:" + _isNewNote);
        if (_isNewNote) {
            _currentID = DataManager.getInstance().createNewNote();
        }
        loadNoteData();
    }

    private void loadNoteData() {
        SQLiteDatabase db = _noteKeeperOpenHelper.getReadableDatabase();

        String selection = NoteKeeperDatabaseContract.NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = { Integer.toString(_currentID) };

        final String[] noteColumns = {NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE, NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TEXT, NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID, NoteKeeperDatabaseContract.NoteInfoEntry._ID};

        _noteCursor = db.query(NoteKeeperDatabaseContract.NoteInfoEntry.TABLE_NAME, noteColumns,selection,selectionArgs,null,null,null );

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

        if(id == R.id.menu_action_next){
            moveNext();
        } else if (id == R.id.menu_action_send_mail) {
            sendEmail();
        } else if (id == R.id.menu_action_save) {
            _isCancelling = false;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_action_next);
        int lastNotePosition = DataManager.getInstance().getNotes().size() - 1;
        menuItem.setEnabled(_currentID < lastNotePosition);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        _currentID++;
        _noteInfo = DataManager.getInstance().getNotes().get(_currentID);

        saveOriginalNote();
        displayNote();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        String subject = "Check this out \"" + ((CourseInfo) _spinnerCourses.getSelectedItem()).getTitle() + "\"" ;
        String body = _textTitle.getText().toString() + "\n" + _textBody.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_isCancelling) {
            if (_isNewNote) {
                DataManager.getInstance().removeNote(_currentID);
            } else {
                _noteInfo = _originalNoteInfo;
            }
            return;
        }

        saveNote();

    }

    private void saveNote() {
        _noteInfo.setCourse((CourseInfo) _spinnerCourses.getSelectedItem());
        _noteInfo.setTitle(_textTitle.getText().toString());
        _noteInfo.setText(_textBody.getText().toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NOTE_INFO, _originalNoteInfo);
    }
}

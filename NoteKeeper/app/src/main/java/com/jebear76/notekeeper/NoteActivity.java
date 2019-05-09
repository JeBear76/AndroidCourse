package com.jebear76.notekeeper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.jebear76.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.jebear76.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_COURSES = 1;
    public final int LOADER_NOTES = 0;
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
    private NoteInfo _originalNoteInfo;
    private List<CourseInfo> _courses;
    private NoteKeeperOpenHelper _noteKeeperOpenHelper;
    private Cursor _noteCursor;
    private SimpleCursorAdapter _courseCursorAdapter;

    private boolean _isCancelling = true;
    private boolean _isDeleting = false;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _spinnerCourses = findViewById(R.id.spinner_courses);

        _courseCursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[] {android.R.id.text1},
                0);
        _courseCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerCourses.setAdapter(_courseCursorAdapter);

        _textTitle = findViewById(R.id.editText_note_title);
        _textBody = findViewById(R.id.editText_note_body);

        loadCoursesAsync();

        readDisplayStateValues();

        loadNoteAsync();
    }

    private void loadCoursesAsync() {
        LoaderManager.getInstance(this).initLoader(LOADER_COURSES,null, this);
    }

    private void loadNoteAsync() {
        LoaderManager.getInstance(this).initLoader(LOADER_NOTES, null, this);
    }

    private void displayNote() {
        if (!_isNewNote && _noteCursor.moveToNext()) {
            int title_column_index = _noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
            int text_column_index = _noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
            int course_id_column_index = _noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);

            _textTitle.setText(_noteCursor.getString(title_column_index));
            _textBody.setText(_noteCursor.getString(text_column_index));
            int courseIndex = getIndexOfCourseId(_noteCursor.getString(course_id_column_index));
            _spinnerCourses.setSelection(courseIndex);
        }
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = _courseCursorAdapter.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        cursor.moveToFirst();
        int index = 0;
        while (!cursor.getString(courseIdPos).equals(courseId)) {
            if(!cursor.moveToNext())
                break;
            index++;
        }
        return index;
    }

    private void restoreOriginalNoteInfo(Bundle savedInstanceState) {
        _originalNoteInfo = savedInstanceState.getParcelable(NOTE_INFO);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();

        _currentID = intent.getIntExtra(NoteActivity.NOTE_ID, POSITION_NOT_SET);

        _isNewNote = _currentID == POSITION_NOT_SET;
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
        }else if(id == R.id.menu_action_delete){
            _isCancelling = false;
            _isDeleting = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_action_delete);

        menuItem.setEnabled(!_isNewNote);

//        int lastNotePosition = DataManager.getInstance().getNotes().size() - 1;
//        menuItem.setEnabled(_currentID < lastNotePosition);

        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

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
            return;
        }
        if (_isDeleting) {
            deleteNote();
        } else {
            if (_isNewNote) {
                createNote();
            } else {
                saveNote();
            }
        }
    }

    private void deleteNote() {
        final String[] whereArgs = {Integer.toString(_currentID)};
        final String whereClause = NoteInfoEntry._ID + " = ?";
        final AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = _noteKeeperOpenHelper.getWritableDatabase();

                db.delete(NoteInfoEntry.TABLE_NAME, whereClause, whereArgs);

                return null;
            }
        };

        task.execute();
    }

    private void createNote(){
        final ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoEntry.COLUMN_COURSE_ID, getSelectedCourseId());
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE, _textTitle.getText().toString());
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT, _textBody.getText().toString());
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = _noteKeeperOpenHelper.getWritableDatabase();
                db.insert(NoteInfoEntry.TABLE_NAME, null, contentValues);
                return null;
            }
        };
        task.execute();
    }

    private void saveNote() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(NoteInfoEntry.COLUMN_COURSE_ID, getSelectedCourseId());
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE, _textTitle.getText().toString());
        contentValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT, _textBody.getText().toString());

        final String[] whereArgs = {Integer.toString(_currentID)};
        final String whereClause = NoteInfoEntry._ID + " = ?";

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = _noteKeeperOpenHelper.getWritableDatabase();
                db.update(NoteInfoEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                return null;
            }
        };
    }

    private String getSelectedCourseId() {
        int position = _spinnerCourses.getSelectedItemPosition();
        Cursor courseCursorAdapterCursor = _courseCursorAdapter.getCursor();
        courseCursorAdapterCursor.moveToPosition(position);
        return courseCursorAdapterCursor.getString(courseCursorAdapterCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(NOTE_INFO, _originalNoteInfo);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader cursorLoader = null;
        if(id == LOADER_NOTES) {
            cursorLoader = createCursorLoaderNotes();
        }
        if(id == LOADER_COURSES){
            cursorLoader = createCursorLoaderCourse();
        }
        return cursorLoader;
    }

    private CursorLoader createCursorLoaderCourse() {
        Uri uri = Uri.parse("content://com.jebear76.notekeeper.provider?query_table_name=" + CourseInfoEntry.TABLE_NAME);

        final String[] courseColumns = {CourseInfoEntry._ID, CourseInfoEntry.COLUMN_COURSE_ID, CourseInfoEntry.COLUMN_COURSE_TITLE};
        final String courseOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE;
        return new CursorLoader(this, uri, courseColumns, null, null, courseOrderBy);
    }

    private Cursor loadNoteData() {
        SQLiteDatabase db = _noteKeeperOpenHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = { Integer.toString(_currentID) };

        final String[] noteColumns = {NoteInfoEntry.COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_NOTE_TEXT, NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};

        return  db.query(NoteInfoEntry.TABLE_NAME, noteColumns,selection,selectionArgs,null,null,null );
    }

    private CursorLoader createCursorLoaderNotes() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                return loadNoteData();
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int loader_id = loader.getId();
        if(loader_id == LOADER_NOTES){
            _noteCursor = data;
            if(_courseCursorAdapter.getCursor() != null){
                displayNote();
            }
            return;
        }
        if(loader_id == LOADER_COURSES){
            _courseCursorAdapter.changeCursor(data);
            if(_noteCursor != null){
                displayNote();
            }
            return;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            if(_noteCursor != null){
                _noteCursor.close();
            }
            return;
        }
        if(loader.getId() == LOADER_NOTES){
            _courseCursorAdapter.changeCursor(null);
            return;
        }
    }
}

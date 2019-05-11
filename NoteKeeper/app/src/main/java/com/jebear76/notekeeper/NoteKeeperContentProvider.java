package com.jebear76.notekeeper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.jebear76.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.jebear76.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.ArrayList;
import java.util.List;

public class NoteKeeperContentProvider extends ContentProvider {
    public static final String QUERY_TABLENAME = "query_table_name";
    NoteKeeperOpenHelper _noteKeeperOpenHelper;

    private static UriMatcher _UriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    static{
        _UriMatcher.addURI(NoteKeeperContentProviderContract.AUTHORITY, NoteKeeperContentProviderContract.Courses.PATH, COURSES);
        _UriMatcher.addURI(NoteKeeperContentProviderContract.AUTHORITY, NoteKeeperContentProviderContract.Notes.PATH, NOTES);
        _UriMatcher.addURI(NoteKeeperContentProviderContract.AUTHORITY, NoteKeeperContentProviderContract.Notes.PATH_EXPANDED, NOTES_EXPANDED);
    }

    public NoteKeeperContentProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        _noteKeeperOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = _noteKeeperOpenHelper.getReadableDatabase();

        int uriMatch = _UriMatcher.match(uri);
        switch (uriMatch){
            case COURSES: {
                cursor = db.query(CourseInfoEntry.TABLE_NAME,projection, selection, selectionArgs,null,null,sortOrder);
                break;
            }
            case NOTES: {
                cursor = db.query(NoteInfoEntry.TABLE_NAME,projection, selection, selectionArgs,null,null, sortOrder);
                break;
            }
            case NOTES_EXPANDED: {
                List<String> columns = new ArrayList<>();
                for (String column: projection) {
                    columns.add(column == BaseColumns._ID || column == NoteKeeperContentProviderContract.CoursesIdColumns.COLUMN_COURSE_ID ? NoteInfoEntry.qualify(column) : column);
                }
                final String joinSelectSource = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME + " ON "
                        + NoteInfoEntry.qualify(NoteInfoEntry.COLUMN_COURSE_ID) + " = " + CourseInfoEntry.qualify(CourseInfoEntry.COLUMN_COURSE_ID);


                cursor = db.query(joinSelectSource,columns.toArray(new String[projection.length]), selection, selectionArgs,null,null, sortOrder);
                break;
            }
        }



        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

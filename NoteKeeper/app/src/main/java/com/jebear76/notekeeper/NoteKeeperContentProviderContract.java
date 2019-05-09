package com.jebear76.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteKeeperContentProviderContract {
    private NoteKeeperContentProviderContract(){}

    public static final String AUTHORITY = "com.jebear76.notekeeper.provider";
    public static Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CoursesColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NotesColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }

    public static final class Courses implements CoursesColumns, BaseColumns,CoursesIdColumns {
        public static final String PATH = "courses";
        public static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

    }

    public static final class Notes implements NotesColumns, BaseColumns, CoursesIdColumns{
        public static final String PATH = "notes";
        public static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);

    }

    public static final class NoteSummary implements CoursesColumns, NotesColumns, CoursesIdColumns, BaseColumns{
        public static final String PATH = "summary";
        public static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}

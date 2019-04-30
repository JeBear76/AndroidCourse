package com.jebear76.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    static DataManager _dm;

    @BeforeClass
    public static void classSetUp() throws Exception{
        _dm = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception{
        _dm.getNotes().clear();
        _dm.initializeExampleNotes();;
    }

    @Test
    public void createNewNote() throws Exception{
        final CourseInfo courseInfo = _dm.getCourse("android_async");
        final String noteTitle = "test";
        final String noteBody = "test body";

        int noteIndex =  _dm.createNewNote();
        NoteInfo newNoteInfo = _dm.getNotes().get(noteIndex);
        newNoteInfo.setCourse(courseInfo);
        newNoteInfo.setText(noteBody);
        newNoteInfo.setTitle(noteTitle);

        NoteInfo compareNoteInfo = _dm.getNotes().get(noteIndex);
        assertEquals(newNoteInfo.getTitle(),compareNoteInfo.getTitle());
        assertEquals(newNoteInfo.getBody(),compareNoteInfo.getBody());
        assertEquals(newNoteInfo.getCourse(),compareNoteInfo.getCourse());
    }

    @Test
    public void createNewNoteWithData() throws Exception{
        final CourseInfo courseInfo = _dm.getCourse("android_async");
        final String noteTitle = "test";
        final String noteBody = "test body";

        int noteIndex =  _dm.createNewNote(courseInfo, noteTitle, noteBody);

        NoteInfo compareNoteInfo = _dm.getNotes().get(noteIndex);
        assertEquals(noteTitle,compareNoteInfo.getTitle());
        assertEquals(noteBody,compareNoteInfo.getBody());
        assertEquals(courseInfo,compareNoteInfo.getCourse());
    }
}
package com.jebear76.notekeeper;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager _dm;

    @BeforeClass
    public static void classSetUp() throws Exception{
        _dm = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<MainActivity> _noteListActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createNewNote(){
        onView(withId(R.id.fab)).perform(click());
        String test_course = "java_lang";
        CourseInfo courseInfo = _dm.getCourse(test_course);
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(courseInfo))).perform(click());
        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(containsString(courseInfo.getTitle()))));
        String test_title = "Test Title";
        onView(withId(R.id.editText_note_title)).perform(typeText(test_title),closeSoftKeyboard()).check(matches(withText(containsString(test_title))));
        String test_body = "Test Body";
        onView(withId(R.id.editText_note_body)).perform(typeText(test_body),closeSoftKeyboard()).check(matches(withText(containsString(test_body))));
        pressBack();
        //int noteIndex = _dm.getNotes().size() - 1;
        //NoteInfo noteInfo = _dm.getNotes().get(noteIndex);
        //assertEquals(test_title,noteInfo.getTitle());
        //assertEquals(test_body,noteInfo.getBody());
        //assertEquals(courseInfo,noteInfo.getCourse());

    }
}
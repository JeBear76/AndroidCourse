package com.jebear76.notekeeper;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class NextThroughNotesTest {
    @Rule
    public ActivityTestRule<MainActivity> _activityTestRule = new ActivityTestRule<>(MainActivity.class);

    static DataManager _dm;

    @BeforeClass
    public static void classSetUp() throws Exception{
        _dm = DataManager.getInstance();
    }

    @Test
    public void NextThroughNote() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));
        onView(withId(R.id.list_notes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        NoteInfo noteInfo;
        for (int index = 0; index < _dm.getNotes().size(); index++) {
            noteInfo = _dm.getNotes().get(index);
            onView(withId(R.id.editText_note_title)).check(matches(withText(noteInfo.getTitle())));
            onView(withId(R.id.editText_note_body)).check(matches(withText(noteInfo.getBody())));
            onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(noteInfo.getCourse().getTitle())));
            if (index < _dm.getNotes().size() - 1)
                onView(allOf(withId(R.id.menu_action_next), isEnabled())).perform(click());
            else
                onView(withId(R.id.menu_action_next)).check(matches(not(isEnabled())));
        }



    }
}
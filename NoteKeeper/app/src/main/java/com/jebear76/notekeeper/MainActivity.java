package com.jebear76.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jebear76.notekeeper.NoteKeeperContentProviderContract.Courses;
import com.jebear76.notekeeper.NoteKeeperContentProviderContract.Notes;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import static com.jebear76.notekeeper.NoteKeeperDatabaseContract.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private NoteRecyclerAdapter _noteRecyclerAdapter;
    private RecyclerView _recyclerView;
    private LinearLayoutManager _linearLayoutManager;
    private CourseRecyclerAdapter _courseRecyclerAdapter;
    private GridLayoutManager _gridLayoutManager;
    private NoteKeeperOpenHelper _noteKeeperOpenHelper;

    @Override
    protected void onDestroy() {
        _noteKeeperOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _noteKeeperOpenHelper = new NoteKeeperOpenHelper(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);

                startActivity(intent);
            }
        });
        LoaderManager.getInstance(this).initLoader(LOADER_NOTES,null,this);

        LoaderManager.getInstance(this).initLoader(LOADER_COURSES,null,this);

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null,this);
        LoaderManager.getInstance(this).restartLoader(LOADER_COURSES, null,this);

        updateNavHeader();

    }

    private void updateNavHeader() {
        NavigationView nav = findViewById(R.id.nav_view);
        View headerView = nav.getHeaderView(0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("user_display_name","");
        String emailAddress = sharedPreferences.getString("user_email_address", "");

        ((TextView)headerView.findViewById(R.id.nav_header_username)).setText(username);
        ((TextView)headerView.findViewById(R.id.nav_header_email)).setText(emailAddress);
    }

    private void initializeDisplayContent() {
        _recyclerView = findViewById(R.id.list_notes);
        _linearLayoutManager = new LinearLayoutManager(this);
        _gridLayoutManager = new GridLayoutManager(this,getResources().getInteger(R.integer.course_grid_span));


        _noteRecyclerAdapter = new NoteRecyclerAdapter(this, null, null);

        _courseRecyclerAdapter = new CourseRecyclerAdapter(this, null);

        displayNotes();

    }

    private void displayCourses() {
        _recyclerView.setLayoutManager(_gridLayoutManager);
        _recyclerView.setAdapter(_courseRecyclerAdapter);
        setNavMenuSelection(R.id.nav_courses);
    }

    private void setNavMenuSelection(int menu_item_id) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(menu_item_id).setChecked(true);
    }

    private void displayNotes() {
        _recyclerView.setLayoutManager(_linearLayoutManager);
        _recyclerView.setAdapter(_noteRecyclerAdapter);

        setNavMenuSelection(R.id.nav_notes);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();
        } else if (id == R.id.nav_share) {
            handleShare();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        Snackbar.make(findViewById(R.id.list_notes),
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social_network","No preference found"),
                Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader cursorLoader = null;
        if(id == LOADER_NOTES){
            cursorLoader = createNoteCursorLoader();
        }
        if(id==LOADER_COURSES){
            cursorLoader = createCourseCursorLoader();
        }
        return cursorLoader;
    }

    private CursorLoader createCourseCursorLoader() {
        Uri uri = Courses.CONTENT_URI;

        final String[] courseColumns = {Courses.COLUMN_COURSE_ID, Courses.COLUMN_COURSE_TITLE};
        final String courseOrderBy = Courses.COLUMN_COURSE_TITLE;

        return new CursorLoader(this, uri, courseColumns,null,null,courseOrderBy);
    }

    private CursorLoader createNoteCursorLoader() {
        Uri uri = Notes.CONTENT_URI_EXPANDED;

        final String[] noteColumns = {Notes.COLUMN_NOTE_TITLE, Notes._ID, Courses.COLUMN_COURSE_TITLE};

        final String noteOrderBy = Courses.COLUMN_COURSE_TITLE + ", " + Notes.COLUMN_NOTE_TITLE;

        return new CursorLoader(this, uri, noteColumns,null,null, noteOrderBy);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int loader_id = loader.getId();

        if(loader_id == LOADER_NOTES){
            _noteRecyclerAdapter.changeNoteCursor(data);
            return;
        }

        if(loader_id == LOADER_COURSES){
            _courseRecyclerAdapter.changeCursor(data);
            return;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        int loader_id = loader.getId();

        if(loader_id == LOADER_NOTES){
            _noteRecyclerAdapter.changeNoteCursor(null);            
            return;
        }
        if(loader_id == LOADER_COURSES){
            _courseRecyclerAdapter.changeCursor(null);
            return;
        }
    }
}

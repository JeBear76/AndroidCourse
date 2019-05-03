package com.jebear76.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NoteRecyclerAdapter _noteRecyclerAdapter;
    private RecyclerView _recyclerView;
    private LinearLayoutManager _linearLayoutManager;
    private CourseRecyclerAdapter _courseRecyclerAdapter;
    private GridLayoutManager _gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        initializeDisplayContent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //_noteInfoArrayAdapter.notifyDataSetChanged();
        _noteRecyclerAdapter.notifyDataSetChanged();

    }

    private void initializeDisplayContent() {

        _recyclerView = findViewById(R.id.list_notes);
        _linearLayoutManager = new LinearLayoutManager(this);
        _gridLayoutManager = new GridLayoutManager(this,2);

        List<NoteInfo> noteInfoList = DataManager.getInstance().getNotes();
        _noteRecyclerAdapter = new NoteRecyclerAdapter(this,noteInfoList);
        List<CourseInfo> courseInfoList = DataManager.getInstance().getCourses();
        _courseRecyclerAdapter = new CourseRecyclerAdapter(this, courseInfoList);
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

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

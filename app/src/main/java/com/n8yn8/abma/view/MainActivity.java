package com.n8yn8.abma.view;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance())
                .commit();

        DbManager.getInstance().getYears(new DbManager.YearsResponse() {
            @Override
            public void onYearsReceived(List<BYear> years) {
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                for (BYear year: years) {
                    db.addYear(year);
                    List<BYear> saveYears = db.getAllYears();
                    Log.d("Nate", "saved = " + saveYears);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.welcome) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, WelcomeFragment.newInstance())
                    .commit();
        } else if (id == R.id.schedule) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ScheduleFragment.newInstance())
                    .commit();
        } else if (id == R.id.notes) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, NoteFragment.newInstance())
                    .commit();
        } else if (id == R.id.conference_info) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, InfoFragment.newInstance())
                    .commit();
        } else if (id == R.id.sponsors) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SponsorsFragment.newInstance())
                    .commit();
        } else if (id == R.id.contact) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ContactFragment.newInstance())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

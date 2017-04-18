package com.n8yn8.abma.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.backendless.BackendlessUser;
import com.n8yn8.abma.App;
import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.Survey;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.model.old.Event;
import com.n8yn8.abma.model.old.Note;
import com.n8yn8.abma.model.old.Paper;
import com.n8yn8.abma.model.old.Schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.n8yn8.abma.R.id.years;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    MenuItem yearsMenuItem;
    Survey survey;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BackendlessUser user = DbManager.getInstance().getCurrentUser();
        navigationView.getMenu().findItem(R.id.logout).setVisible(user != null);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        List<BYear> saveYears = db.getAllYears();
        if (saveYears.size() == 0) {
            loadBackendless(db);
        } else {
            updateSurvey();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        yearsMenuItem = menu.findItem(years);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case years:
                showYearsPicker();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBackendless(final DatabaseHandler db) {
        ScheduleFragment fragment = getScheduleFragment();
        if (fragment != null) {
            fragment.setLoading(true);
        }
        DbManager.getInstance().getYears(this, new DbManager.YearsResponse() {
            @Override
            public void onYearsReceived(List<BYear> years) {

                for (BYear year: years) {
                    db.addYear(year);
                }
                checkOldNotes(db);
                updateSurvey();

                ScheduleFragment fragment = getScheduleFragment();
                if (fragment != null) {
                    fragment.reload();
                }
            }
        });
    }

    private void checkOldNotes(DatabaseHandler db) {
        Map<Note, Pair<Event, Paper>> oldMap = ((App) getApplicationContext()).getOldNotes();
        if (oldMap == null) {
            return;
        }

        Schedule schedule = ((App)getApplication()).getOldSchedule();

        for (Note oldNote: oldMap.keySet()) {
            schedule.setDayIndex(oldNote.getDayId());
            Pair<Event, Paper> pair = oldMap.get(oldNote);
            Event oldEvent = pair.first;
            String newEventId = null;
            Paper oldPaper = pair.second;
            String newPaperId = null;
            if (oldEvent != null) {
                BEvent newEvent = db.getMatchedEvent(schedule.getCurrentDate(), oldEvent);
                if (newEvent != null) {
                    newEventId = newEvent.getObjectId();
                }
            }
            if (oldPaper != null) {
                BPaper newPaper = db.getMatchedPaper(oldPaper);
                if (newPaper != null) {
                    newPaperId = newPaper.getObjectId();
                }
            }
            BNote newNote = new BNote();
            newNote.setContent(oldNote.getContent());
            newNote.setEventId(newEventId);
            newNote.setPaperId(newPaperId);
            db.addNote(newNote);
        }
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

        yearsMenuItem.setVisible(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.welcome) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, WelcomeFragment.newInstance())
                    .commit();
        } else if (id == R.id.schedule) {
            yearsMenuItem.setVisible(true);
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
        } else if (id == R.id.survey) {
            String urlString = survey.getSurveyUrl();
            if (urlString != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
                Utils.logSurvey();
            }
        } else if (id == R.id.logout) {
            DbManager.getInstance().logout();
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showYearsPicker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Year");
        final YearSelectorView view = new YearSelectorView(this);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedYear = view.getSelectedYear();
                MainActivity.this.updateSelectedYear(selectedYear);
            }
        });
        builder.show();
    }

    private void updateSelectedYear(String year) {
        ScheduleFragment fragment = getScheduleFragment();
        if (fragment != null) {
            fragment.setYear(year);
        }
    }

    @Nullable
    private ScheduleFragment getScheduleFragment() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment instanceof ScheduleFragment) {
                return (ScheduleFragment) fragment;
            }
        }
        return null;
    }

    private void updateSurvey() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        survey = db.getLatestSurvey();
        Date now = new Date();
        if (now.after(survey.getSurveyStart()) && now.before(survey.getSurveyEnd())) {
            navigationView.getMenu().findItem(R.id.survey).setVisible(true);
        }
    }
}

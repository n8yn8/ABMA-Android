package com.n8yn8.abma.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.backendless.BackendlessUser;
import com.n8yn8.abma.App;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.ConvertUtil;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.model.old.Event;
import com.n8yn8.abma.model.old.Note;
import com.n8yn8.abma.model.old.Paper;
import com.n8yn8.abma.model.old.Schedule;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import static com.n8yn8.abma.R.id.years;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    MenuItem yearsMenuItem;
    MenuItem yearInfoMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BackendlessUser user = DbManager.getInstance().getCurrentUser();
        navigationView.getMenu().findItem(R.id.logout).setVisible(user != null);
        yearInfoMenuItem = navigationView.getMenu().findItem(R.id.conference_info);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<Year> savedYears = AppDatabase.getInstance(getApplicationContext()).yearDao().getYears();
        if (savedYears.size() == 0) {
            loadBackendless(false);
        } else {
            SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            if (preferences.getBoolean("PushReceived", false)) {
                loadBackendless(true);
            } else {
                updateYearInfo();
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("PushReceived"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadBackendless(true);
        }
    };

    private void loadBackendless(final boolean isUpdate) {
        SharedPreferences preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("PushReceived", false);
        editor.apply();

        final ScheduleFragment fragment = getScheduleFragment();
        if (fragment != null) {
            fragment.setLoading(true);
        }
        DbManager.getInstance().getYears(this, new DbManager.Callback<List<BYear>>() {
            @Override
            public void onDone(List<BYear> years, String error) {

                if (error != null) {
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }

                for (BYear year: years) {
                    AppDatabase.getInstance(getApplicationContext()).yearDao().insert(ConvertUtil.convert(year));
                }
                updateYearInfo();

                ScheduleFragment fragment = getScheduleFragment();
                if (fragment != null) {
                    fragment.setLoading(false);
                    fragment.reload(isUpdate);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        } else if (id == R.id.maps) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MapsFragment.newInstance())
                    .commit();
        } else if (id == R.id.sponsors) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SponsorsFragment.newInstance())
                    .commit();
        } else if (id == R.id.contact) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ContactFragment.newInstance())
                    .commit();
        } else if (id == R.id.logout) {
            DbManager.getInstance().logout(null);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            if (fragment instanceof ScheduleFragment) {
                return (ScheduleFragment) fragment;
            }
        }
        return null;
    }

    private void updateYearInfo() {
        Year latestYear = AppDatabase.getInstance(getApplicationContext()).yearDao().getLastYear();
        if (latestYear == null || yearInfoMenuItem == null) {
            return;
        }
        int year = latestYear.name;
        yearInfoMenuItem.setTitle(year + " Info");
    }

    private static class DbTask extends AsyncTask<Void, Void, List<Year>> {
        private WeakReference<MainActivity> activity;

        public DbTask(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected List<Year> doInBackground(Void... voids) {
            List<Year> years = AppDatabase.getInstance(activity.get()).yearDao().getYears();
            for (Year year : years) {
                Log.d("Nate", "year = " + year);
            }
//            List<com.n8yn8.abma.model.entities.Event> events = AppDatabase.getInstance(activity.get()).eventDao().getEvents();
//            for (com.n8yn8.abma.model.entities.Event event : events) {
//                Log.d("Nate", "event = " + event);
//            }
            return years;
        }

        @Override
        protected void onPostExecute(List<Year> years) {



            super.onPostExecute(years);
        }
    }
}

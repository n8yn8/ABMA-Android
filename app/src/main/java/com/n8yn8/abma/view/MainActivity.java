package com.n8yn8.abma.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backendless.BackendlessUser;
import com.google.android.material.navigation.NavigationView;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Year;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static com.n8yn8.abma.R.id.years;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    MenuItem yearsMenuItem;
    MenuItem yearInfoMenuItem;
    private MainViewModel viewModel;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            viewModel.loadBackendless();
        }
    };

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

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getYear().observe(this, new Observer<Year>() {
            @Override
            public void onChanged(Year year) {
                if (year == null || yearInfoMenuItem == null) {
                    return;
                }
                int yearName = year.name;
                yearInfoMenuItem.setTitle(yearName + " Info");
            }
        });
        viewModel.getYearNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> names) {
                showYearsPicker(names);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                viewModel.requestYearNames();
        }
        return super.onOptionsItemSelected(item);
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

    private void showYearsPicker(List<String> yearNames) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Year");
        final YearSelectorView view = new YearSelectorView(this, yearNames);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedYear = view.getSelectedYear();
                viewModel.selectYear(selectedYear);
            }
        });
        builder.show();
    }

}

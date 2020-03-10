package com.n8yn8.abma.view;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.view.adapter.ScheduleListAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {
    public static final String TAG = "ScheduleFragment";

    ScheduleListAdapter adapter;
    AppDatabase db;
    MainViewModel mainViewModel;

    ImageButton backButton;
    ImageButton nextButton;
    TextView dateTextView;
    ListView scheduleListView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<Event> day;
    long displayDateMillis;
    Year selectedYear;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        backButton = rootView.findViewById(R.id.prevDayButton);
        nextButton = rootView.findViewById(R.id.nextDayButton);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        scheduleListView = rootView.findViewById(R.id.scheduleListView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event previousEvent = db.eventDao().getEventBefore(selectedYear.objectId, displayDateMillis);
                if (previousEvent != null) {
                    displayDateMillis = Utils.getStartOfDay(previousEvent.startDate);
                    displayDay();
                } else {
                    Toast.makeText(getContext(), "First event reached", Toast.LENGTH_SHORT).show();
                }

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event nextEvent = db.eventDao().getEventAfter(selectedYear.objectId, displayDateMillis + TimeUnit.DAYS.toMillis(1));
                if (nextEvent != null) {
                    displayDateMillis = Utils.getStartOfDay(nextEvent.startDate);
                    displayDay();
                } else {
                    Toast.makeText(getContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = adapter.getItem(position);
                if (event != null) {
                    EventActivity.start(getActivity(), event.objectId, null);
                }
            }
        });

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainViewModel.loadBackendless(true);
            }
        });

        reload(false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getYear().observe(getViewLifecycleOwner(), new Observer<Year>() {
            @Override
            public void onChanged(Year year) {
                selectedYear = year;
                reload(false);
            }
        });

        mainViewModel.isLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });
    }

    public void reload(boolean isUpdate) {
        if (selectedYear == null) {
            selectedYear = db.yearDao().getLastYear();
        }
        setUpYear(isUpdate);
    }

    private void setUpYear(boolean isUpdate) {
        if (selectedYear != null) {
            List<Event> events = db.eventDao().getEvents(selectedYear.objectId);
            if (events.size() == 0 || isUpdate) {
//TODO:                setLoading(true);
                DbManager.getInstance().getEvents(selectedYear.objectId, new DbManager.Callback<List<BEvent>>() {
                    @Override
                    public void onDone(List<BEvent> bEvents, String error) {
                        Utils.saveEvents(getContext(), selectedYear.objectId, bEvents);
//TODO:                        setLoading(false);
                        reload(false);
                    }
                });
            } else {
                Event firstEvent = events.get(0);
                displayDateMillis = Utils.getStartOfDay(firstEvent.startDate);
            }
        }
        displayDay();
    }

    public void displayDay() {
        Log.d("Nate", "start = " + displayDateMillis + " end = " + (displayDateMillis + TimeUnit.HOURS.toMillis(24)));
        day = db.eventDao().getAllEventsFor(displayDateMillis, displayDateMillis + TimeUnit.HOURS.toMillis(24));
        if (day.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateTextView.setText(dateFormat.format(day.get(0).startDate));
            dateTextView.setVisibility(View.VISIBLE);
        } else {
            dateTextView.setVisibility(View.INVISIBLE);
        }
        adapter = new ScheduleListAdapter(getActivity(), day);
        scheduleListView.setAdapter(adapter);
    }
}

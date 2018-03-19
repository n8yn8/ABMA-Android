package com.n8yn8.abma.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.old.DatabaseHandler;
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
    DatabaseHandler db;

    ImageButton backButton;
    ImageButton nextButton;
    TextView dateTextView;
    ListView scheduleListView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<BEvent> day;
    long displayDateMillis;

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
        if (getArguments() != null) {
        }

        db = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        backButton = (ImageButton) rootView.findViewById(R.id.prevDayButton);
        nextButton = (ImageButton) rootView.findViewById(R.id.nextDayButton);
        dateTextView = (TextView) rootView.findViewById(R.id.dateTextView);
        scheduleListView = (ListView) rootView.findViewById(R.id.scheduleListView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDateMillis -= TimeUnit.HOURS.toMillis(24);
                displayDay();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDateMillis += TimeUnit.HOURS.toMillis(24);
                displayDay();
            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BEvent event = adapter.getItem(position);
                EventActivity.start(getActivity(), event.getObjectId(), null);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DbManager.getInstance().getYears(getContext(), new DbManager.Callback<List<BYear>>() {
                    @Override
                    public void onDone(List<BYear> years, String error) {
                        setLoading(false);

                        if (error != null) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                        }

                        Utils.saveYears(getContext(), years);
                        reload();
                    }
                });
            }
        });

        reload();

        return rootView;
    }

    public void setLoading(final boolean isLoading) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });

    }

    public void reload() {
        BYear year = db.getLastYear();
        setUpYear(year);
    }

    private void setUpYear(final BYear year) {
        if (year != null) {
            List<BEvent> events = year.getEvents();
            if (events.size() == 0) {
                setLoading(true);
                DbManager.getInstance().getEvents(year.getObjectId(), new DbManager.Callback<List<BEvent>>() {
                    @Override
                    public void onDone(List<BEvent> bEvents, String error) {
                        Utils.saveEvents(getContext(), year.getObjectId(), bEvents);
                        setLoading(false);
                        reload();
                    }
                });
            } else {
                BEvent firstEvent = events.get(0);
                displayDateMillis = Utils.getStartOfDay(firstEvent.getStartDate());
            }
        }
        displayDay();
    }

    public void displayDay() {
        Log.d("Nate", "start = " + displayDateMillis + " end = " + (displayDateMillis + TimeUnit.HOURS.toMillis(24)));
        day = db.getAllEventsFor(displayDateMillis, displayDateMillis + TimeUnit.HOURS.toMillis(24));
        if (day.size() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateTextView.setText(dateFormat.format(day.get(0).getStartDate()));
            dateTextView.setVisibility(View.VISIBLE);
        } else {
            dateTextView.setVisibility(View.INVISIBLE);
        }
        adapter = new ScheduleListAdapter(getActivity(), day);
        scheduleListView.setAdapter(adapter);
    }

    public void setYear(String name) {
        BYear selectedYear = db.getYearByName(name);
        setUpYear(selectedYear);
    }
}

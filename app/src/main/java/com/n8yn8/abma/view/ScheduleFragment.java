package com.n8yn8.abma.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.view.adapter.ScheduleListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    ImageButton backButton;
    ImageButton nextButton;
    TextView dateTextView;
    ListView scheduleListView;
    SwipeRefreshLayout swipeRefreshLayout;

    List<Event> day;
    long displayDateMillis;
    String selectedYearName;

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
                DbManager.getInstance().getYears(getContext(), new DbManager.Callback<List<BYear>>() {
                    @Override
                    public void onDone(List<BYear> years, String error) {
                        setLoading(false);

                        if (error != null) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                        }

                        Utils.saveYears(getContext(), years);
                        reload(years.size() > 0);
                    }
                });
            }
        });

        reload(false);

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

    public void reload(boolean isUpdate) {
        Year year;
        if (TextUtils.isEmpty(selectedYearName)) {
            year = db.yearDao().getLastYear();
        } else {
            year = db.yearDao().getYearByName(selectedYearName);
        }
        setUpYear(year, isUpdate);
    }

    private void setUpYear(final Year year, boolean isUpdate) {
        if (year != null) {
            List<Event> events = db.eventDao().getEvents(year.objectId);
            if (events.size() == 0 || isUpdate) {
                setLoading(true);
                DbManager.getInstance().getEvents(year.objectId, new DbManager.Callback<List<BEvent>>() {
                    @Override
                    public void onDone(List<BEvent> bEvents, String error) {
                        Utils.saveEvents(getContext(), year.objectId, bEvents);
                        setLoading(false);
                        reload(false);
                    }
                });
            } else {
                Event firstEvent = events.get(0);
                displayDateMillis = Utils.getStartOfDay(new Date(firstEvent.startDate));
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

    public void setYear(String name) {
        selectedYearName = name;
        reload(false);
    }
}

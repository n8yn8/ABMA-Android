package com.n8yn8.abma;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends android.support.v4.app.Fragment {
    private final String TAG = "Schedule";
    ScheduleListAdapter adapter;

    ImageButton backButton;
    ImageButton nextButton;
    TextView dateTextView;
    ListView scheduleListView;

    Schedule schedule;
    ArrayList<Event> day;

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
                Log.d(TAG, "back button clicked");
                day = schedule.getPrevDay();
                if (day != null) {
                    displayDay(day);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "next button clicked");
                day = schedule.getNextDay();
                if (day != null) {
                    displayDay(day);
                }
            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                schedule.setCurrentEventIndex(position);
                Cache.getInstance().cacheSchedule(schedule);
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        schedule = Cache.getInstance().getSchedule();
        day = schedule.getCurrentDay();
        displayDay(day);
    }

    public void displayDay(ArrayList<Event> day) {
        //TODO: figure out why adapter.notifdatasetchanged doesn't work
        dateTextView.setText(schedule.getCurrentDate());
        adapter = new ScheduleListAdapter(getActivity(), day);
        scheduleListView.setAdapter(adapter);
    }
}

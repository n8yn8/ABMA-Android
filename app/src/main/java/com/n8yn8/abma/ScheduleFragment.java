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

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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
    NSDictionary scheduleDict;
    int scheduleIndex = 0;
    List<String> eventDays;
    ArrayList<Event> day;
    Schedule schedule;

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
                scheduleIndex--;
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "next button clicked");
                scheduleIndex++;
                day = schedule.getNextDay();
                adapter.clear();
                for(Event event: day) {
                    adapter.add(event);
                }

            }
        });

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = adapter.getItem(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                intent.putExtra("EXTRA_EVENT_TITLE", event.getTitle());
                intent.putExtra("EXTRA_EVENT_SUBTITLE", event.getSubtitle());
                intent.putExtra("EXTRA_EVENT_TIME",event.getTime());
                intent.putExtra("EXTRA_EVENT_LOCATION",event.getPlace());
                intent.putExtra("EXTRA_EVENT_DETAIL",event.getDetails());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            InputStream is = getResources().openRawResource(R.raw.event_list);
            scheduleDict = (NSDictionary) PropertyListParser.parse(is);
        } catch(Exception ex) {
            Log.e(TAG, "" + ex.getLocalizedMessage());
        }

        schedule = new Schedule(scheduleDict);
        eventDays = new ArrayList<>(scheduleDict.keySet());
        String dayKey = eventDays.get(scheduleIndex);
        NSObject[] dayNSArray = ((NSArray) scheduleDict.objectForKey(dayKey)).getArray();
        day = new ArrayList<>();
        for(NSObject eventNSObject: dayNSArray) {
            Event event = new Event(eventNSObject);
            day.add(event);
        }
        adapter = new ScheduleListAdapter(getActivity(), day);
        scheduleListView.setAdapter(adapter);
    }
}

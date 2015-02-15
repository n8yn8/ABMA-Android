package com.n8yn8.abma;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends android.support.v4.app.Fragment {
    private final String TAG = "Schedule";

    ImageButton backButton;
    ImageButton nextButton;
    TextView dateTextView;
    ListView scheduleListView;
    NSDictionary schedule;
    int scheduleIndex = 0;
    List<String> eventDays;

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
        ImageButton backButton = (ImageButton) rootView.findViewById(R.id.prevDayButton);
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.nextDayButton);
        TextView dateTextView = (TextView) rootView.findViewById(R.id.dateTextView);
        ListView scheduleListView = (ListView) rootView.findViewById(R.id.scheduleListView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "back button clicked");
                scheduleIndex--;
                setDay();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "next button clicked");
                scheduleIndex++;
                setDay();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            InputStream is = getResources().openRawResource(R.raw.event_list);
            schedule = (NSDictionary) PropertyListParser.parse(is);
            Set<String> eventDaysSet = schedule.keySet();
            eventDays = new ArrayList<String>(eventDaysSet);
        } catch(Exception ex) {
            Log.e(TAG, "" + ex.getLocalizedMessage());
        }
    }

    private void setDay() {
        String dayKey = eventDays.get(scheduleIndex);
        NSObject[] day = ((NSArray) schedule.objectForKey(dayKey)).getArray();
        for(NSObject object: day) {
            Map<String, String> event = (Map<String, String>) object.toJavaObject();
            Log.d(TAG, "object class = " + event.toString());
        }
    }
}

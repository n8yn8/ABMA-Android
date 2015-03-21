package com.n8yn8.abma;

import android.app.Application;
import android.util.Log;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

import java.io.InputStream;

/**
 * Created by Nate on 2/18/15.
 */
public class App extends Application {

    private final String TAG = "App";

    Schedule schedule;

    @Override
    public void onCreate() {
        super.onCreate();
        NSDictionary scheduleDict;
        try {
            InputStream is = getResources().openRawResource(R.raw.y2015);
            scheduleDict = (NSDictionary) PropertyListParser.parse(is);
            schedule = new Schedule(scheduleDict);
            Cache.getInstance().cacheSchedule(schedule);
        } catch(Exception ex) {
            Log.e(TAG, "" + ex.getLocalizedMessage());
        }
    }
}

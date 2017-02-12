package com.n8yn8.abma;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.n8yn8.abma.model.Cache;
import com.n8yn8.abma.model.Schedule;

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
            InputStream is = getResources().openRawResource(R.raw.y2016);
            scheduleDict = (NSDictionary) PropertyListParser.parse(is);
            schedule = new Schedule(scheduleDict);
            Cache.getInstance().cacheSchedule(schedule);
        } catch(Exception ex) {
            Log.e(TAG, "" + ex.getLocalizedMessage());
        }

        Backendless.initApp(this, "4F90A91F-3E58-5E4D-FF43-A0BA7FE1D500", "C6BA3234-4552-5DB7-FF8C-18477C80CC00", "v1");
    }
}

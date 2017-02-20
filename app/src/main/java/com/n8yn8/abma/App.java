package com.n8yn8.abma;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.n8yn8.abma.model.old.Cache;
import com.n8yn8.abma.model.old.Schedule;

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

        Backendless.initApp(this, "6AC37915-D986-26C2-FF1C-B0B3ACCB6A00", "145212DB-A903-6C49-FF02-1C70A1BD0A00", "v1");
    }
}

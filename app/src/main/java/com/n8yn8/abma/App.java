package com.n8yn8.abma;

import android.app.Application;
import android.support.v4.util.Pair;
import android.util.Log;

import com.backendless.Backendless;
import com.crashlytics.android.Crashlytics;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.old.Event;
import com.n8yn8.abma.model.old.Note;
import com.n8yn8.abma.model.old.Paper;
import com.n8yn8.abma.model.old.Schedule;

import java.io.InputStream;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Nate on 2/18/15.
 */
public class App extends Application {

    private final String TAG = "App";

    private Map<Note, Pair<Event, Paper>> oldNotes = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        Backendless.initApp(this, "6AC37915-D986-26C2-FF1C-B0B3ACCB6A00", "145212DB-A903-6C49-FF02-1C70A1BD0A00", "v1");
        DbManager.getInstance().checkUser();
        DbManager.getInstance().registerPush();
    }

    public Schedule getOldSchedule() {
        NSDictionary scheduleDict;
        try {
            InputStream is = getResources().openRawResource(R.raw.y2016);
            scheduleDict = (NSDictionary) PropertyListParser.parse(is);
            Schedule schedule = new Schedule(scheduleDict);
            return schedule;
        } catch(Exception ex) {
            Log.e(TAG, "" + ex.getLocalizedMessage());
            return null;
        }
    }

    public Map<Note, Pair<Event, Paper>> getOldNotes() {
        return oldNotes;
    }

    public void setOldNotes(Map<Note, Pair<Event, Paper>> oldNotes) {
        this.oldNotes = oldNotes;
    }
}

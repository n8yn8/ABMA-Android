package com.n8yn8.abma;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
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
    private ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        Backendless.initApp(this, "7D06F708-89FA-DD86-FF95-C51A10425A00", "AA32ED18-4FEC-569C-FF5F-AE0F2F571E00"); //Prod
//        Backendless.initApp(this, "05CFD853-3BFF-40F5-BAD0-E9CE8FA56630", "AA32ED18-4FEC-569C-FF5F-AE0F2F571E00"); //Test
        DbManager.getInstance().checkUser(new DbManager.CheckUserCallback() {
            @Override
            public void onDone() {
                DbManager.getInstance().registerPush();
            }
        });

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(4 * 1024 * 1024); //4MB
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public Schedule getOldSchedule() {
        NSDictionary scheduleDict;
        try {
            InputStream is = getResources().openRawResource(R.raw.y2016);
            scheduleDict = (NSDictionary) PropertyListParser.parse(is);
            return new Schedule(scheduleDict);
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

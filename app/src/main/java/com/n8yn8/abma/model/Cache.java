package com.n8yn8.abma.model;

import android.util.LruCache;

/**
 * Created by Nate on 2/16/15.
 */
public class Cache {

    private String TAG = "Cache";

    private  static Cache instance;
    private LruCache<Object, Object> lruCache;

    private Cache() {
        lruCache = new LruCache<Object, Object>(1024);
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public LruCache<Object, Object> getLruCache() {
        return lruCache;
    }

    public void cacheSchedule(Schedule schedule) {
        getLruCache().put("schedule", schedule);
    }

    public Schedule getSchedule() {
        return (Schedule)getLruCache().get("schedule");
    }

}

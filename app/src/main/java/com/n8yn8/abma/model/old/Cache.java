package com.n8yn8.abma.model.old;

import android.util.LruCache;

/**
 * Created by Nate on 2/16/15.
 */
public class Cache {

    private String TAG = "Cache";

    private static Cache instance;
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

}

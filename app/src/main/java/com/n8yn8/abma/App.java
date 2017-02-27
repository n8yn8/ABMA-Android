package com.n8yn8.abma;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Created by Nate on 2/18/15.
 */
public class App extends Application {

    private final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.initApp(this, "6AC37915-D986-26C2-FF1C-B0B3ACCB6A00", "145212DB-A903-6C49-FF02-1C70A1BD0A00", "v1");
    }
}

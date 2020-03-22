package com.n8yn8.abma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.backendless.push.BackendlessFCMService;

/**
 * Created by Nate on 3/16/17.
 */

public class PushService extends BackendlessFCMService {

    @Override
    public boolean onMessage(final Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        Log.d("Nate", "onMessaage = " + message);

        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("PushReceived", true);
        editor.apply();

        Intent localIntent = new Intent("PushReceived");
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

        return true;
    }
}

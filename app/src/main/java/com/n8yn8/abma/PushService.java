package com.n8yn8.abma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.backendless.push.BackendlessPushService;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.view.MainActivity;

import java.util.List;

/**
 * Created by Nate on 3/16/17.
 */

public class PushService extends BackendlessPushService {

    @Override
    public void onRegistered(Context context, String registrationId) {
        super.onRegistered(context, registrationId);
        Log.d("Nate", "onRegistered " + registrationId);
    }

    @Override
    public void onUnregistered(Context context, Boolean unregistered) {
        super.onUnregistered(context, unregistered);
        Log.d("Nate", "onUnregistered");
    }

    @Override
    public boolean onMessage(final Context context, Intent intent) {

        String message = intent.getStringExtra( "message" );
        Log.d("Nate", "onMessaage = " + message);

        DbManager.getInstance().getYears(context, new DbManager.YearsResponse() {
            @Override
            public void onYearsReceived(List<BYear> years, String error) {
                if (error == null) {
                    Utils.saveYears(context, years);
                }
            }
        });

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("ABMA Update")
                .setContentText(message)
                .setAutoCancel(true);

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        return false;
    }

    @Override
    public void onError(Context context, String message) {
        super.onError(context, message);
        Log.d("Nate", "onError");
    }
}

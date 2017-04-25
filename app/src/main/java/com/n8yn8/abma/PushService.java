package com.n8yn8.abma;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.backendless.push.BackendlessPushService;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;

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

        return super.onMessage(context, intent);
    }

    @Override
    public void onError(Context context, String message) {
        super.onError(context, message);
        Log.d("Nate", "onError");
    }
}

package com.n8yn8.abma;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.n8yn8.abma.model.backendless.BEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nate on 2/27/17.
 */

public class Utils {

    public static String getTimes(BEvent event) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String times = timeFormatter.format(event.getStartDate());
        if (event.getEndDate() != null) {
            times += " - " + timeFormatter.format(event.getEndDate());
        }
        return times;
    }

    private static final String LAST_UPDATED = "lastUpdated";
    private static final String PREFS = "prefs";

    @Nullable
    public static Date getLastUpdated(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long dateMillis = sharedPref.getLong(LAST_UPDATED, 0);
        return new Date(dateMillis);
    }

    public static void updateLastUpdated(Context context, Date date) {
        Date prevUpdate = getLastUpdated(context);
        if (prevUpdate == null) {
            saveLastUpdated(context, date);
        } else {
            if (date.getTime() > prevUpdate.getTime()) {
                saveLastUpdated(context, date);
            }
        }
    }

    private static void saveLastUpdated(Context context, Date date) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_UPDATED, date.getTime());
        editor.apply();
    }
}

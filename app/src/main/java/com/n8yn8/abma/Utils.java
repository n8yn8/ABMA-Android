package com.n8yn8.abma;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.n8yn8.abma.model.entities.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nate on 2/27/17.
 */

public class Utils {

    private static final String LAST_UPDATED = "lastUpdated";
    private static final String PREFS = "prefs";

    public static String getTimes(Event event) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String times = timeFormatter.format(event.startDate);
        if (event.endDate != null) {
            times += " - " + timeFormatter.format(event.endDate);
        }
        return times;
    }

    @Nullable
    public static Date getLastUpdated(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        long dateMillis = sharedPref.getLong(LAST_UPDATED, 0);
        if (dateMillis == 0) {
            return null;
        }
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

    public static void logError(String method, String error) {
        if (!BuildConfig.DEBUG) {
            //TODO: add analytics
        } else {
            Log.e("Utils", method + " had error: " + error);
        }
    }

    public static long getStartOfDay(long timeMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}

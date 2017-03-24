package com.n8yn8.abma;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
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

    public static void logSignUp(boolean success) {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logSignUp(new SignUpEvent()
                    .putMethod("Email")
                    .putSuccess(success));
        }
    }

    public static void logLogIn(boolean success) {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logLogin(new LoginEvent()
                    .putMethod("Email")
                    .putSuccess(success));
        }
    }

    public static void logError(String method, String error) {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Error")
                    .putCustomAttribute("method", method)
                    .putCustomAttribute("error", error));
        } else {
            Log.e("Utils", method + " had error: " + error);
        }
    }
}

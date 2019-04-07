package com.n8yn8.abma;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.ConvertUtil;
import com.n8yn8.abma.model.MyDateTypeAdapter;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BMap;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BSurvey;
import com.n8yn8.abma.model.backendless.BYear;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Nate on 2/27/17.
 */

public class Utils {

    public static String getTimes(Event event) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String times = timeFormatter.format(event.startDate);
        if (event.endDate != null) {
            times += " - " + timeFormatter.format(event.endDate);
        }
        return times;
    }

    private static final String LAST_UPDATED = "lastUpdated";
    private static final String PREFS = "prefs";

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
            Answers.getInstance().logCustom(new CustomEvent("Error " + method)
                    .putCustomAttribute("error", error));
        } else {
            Log.e("Utils", method + " had error: " + error);
        }
    }

    public static void logSurvey() {
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logCustom(new CustomEvent("Survey"));
        } else {
            Log.e("Utils", "survey clicked");
        }
    }

    public static void saveYears(Context context, List<BYear> years) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        for (BYear year: years) {
            db.yearDao().insert(ConvertUtil.convert(year));
            saveSurveys(context, year);
            saveMaps(context, year);
        }
    }

    private static void saveSurveys(Context context, BYear year) {
        String surveysString = year.getSurveys();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
                .create();
        List<BSurvey> surveys = gson.fromJson(surveysString, new TypeToken<List<BSurvey>>(){}.getType());
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        db.surveyDao().insert(ConvertUtil.convertSurveys(surveys, year.getObjectId()));
    }

    private static void saveMaps(Context context, BYear year) {
        String mapsString = year.getMaps();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
                .create();
        List<BMap> maps = gson.fromJson(mapsString, new TypeToken<List<BMap>>(){}.getType());
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        db.mapDao().insert(ConvertUtil.convertMaps(maps, year.getObjectId()));
    }

    public static void saveEvents(Context context, String yearId, List<BEvent> events) {
        final AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        List<Event> localEvents = db.eventDao().getEvents(yearId);
        for (Event localEvent : localEvents) {
            boolean found = false;
            for (BEvent remoteEvent : events) {
                if (remoteEvent.getObjectId().equals(localEvent.objectId)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                db.eventDao().delete(localEvent);
            }
        }

        db.eventDao().insert(ConvertUtil.convertEvents(events, yearId));
        for (final BEvent event : events) {
            if (event.getPapersCount() != 0) {
                DbManager.getInstance().getPapers(event.getObjectId(), new DbManager.Callback<List<BPaper>>() {
                    @Override
                    public void onDone(List<BPaper> bPapers, String error) {
                        db.paperDao().insert(ConvertUtil.convertPapers(bPapers, event.getObjectId()));
                    }
                });
            }
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

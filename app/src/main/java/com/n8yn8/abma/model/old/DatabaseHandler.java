package com.n8yn8.abma.model.old;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.n8yn8.abma.App;
import com.n8yn8.abma.model.MyDateTypeAdapter;
import com.n8yn8.abma.model.Survey;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.backendless.BSurvey;
import com.n8yn8.abma.model.backendless.BYear;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Nate on 3/14/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "abma";

    // Table names
    private static final String TABLE_YEARS = "years";
    private static final String TABLE_SPONSORS = "sponsors";
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_PAPERS = "papers";
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_SURVEYS = "surveys";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_OBJECT_ID = "object_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_INFO = "info";
    private static final String KEY_WELCOME = "welcome";
    private static final String KEY_URL = "url";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_YEAR_ID = "year_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_PLACE = "place";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_PAPER_ID = "paper_id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_SYNOPSIS = "synopsis";
    private static final String KEY_ORDER = "order_by";
    private static final String KEY_NOTE_CONTENT = "note_content";
    private static final String KEY_CREATED = "created_at";
    private static final String KEY_UPDATED = "updated_at";
    private static final String KEY_SURVEY_URL = "survey_url";
    private static final String KEY_SURVEY_START = "survey_start";
    private static final String KEY_SURVEY_END = "survey_end";

    //old Notes:
    private static final String KEY_DAY_INDEX = "day_index";
    private static final String KEY_EVENT_TITLE = "event_title";

    Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private String CREATE_PAPERS_TABLE = "CREATE TABLE " + TABLE_PAPERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_EVENT_ID + " STRING,"
            + KEY_TITLE + " STRING,"
            + KEY_AUTHOR + " STRING,"
            + KEY_SYNOPSIS + " TEXT,"
            + KEY_ORDER + " INT,"
            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
            + ")";

    private String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_YEAR_ID + " STRING,"
            + KEY_DETAILS + " TEXT,"
            + KEY_END_DATE + " INT,"
            + KEY_START_DATE + " INT,"
            + KEY_PLACE + " STRING,"
            + KEY_TITLE + " STRING,"
            + KEY_SUBTITLE + " STRING,"
            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
            + ")";

    private String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_EVENT_ID + " STRING,"
            + KEY_PAPER_ID + " STRING,"
            + KEY_NOTE_CONTENT + " TEXT,"
            + KEY_CREATED + " INT,"
            + KEY_UPDATED + " INT,"
            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
            + ")";

    private String CREATE_YEARS_TABLE = "CREATE TABLE " + TABLE_YEARS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_NAME + " INTEGER,"
            + KEY_INFO + " TEXT,"
            + KEY_WELCOME + " TEXT,"
            + KEY_SURVEY_URL + " TEXT,"
            + KEY_SURVEY_START + " INT,"
            + KEY_SURVEY_END + " INT,"
            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
            + ")";

    private String CREATE_SPONSORS_TABLE = "CREATE TABLE " + TABLE_SPONSORS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_YEAR_ID + " STRING,"
            + KEY_IMAGE_URL + " STRING,"
            + KEY_URL + " STRING,"
            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
            + ")";

    private String CREATE_SURVEYS_TABLE = "CREATE TABLE " + TABLE_SURVEYS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_YEAR_ID + " STRING,"
            + KEY_TITLE + " STRING,"
            + KEY_DETAILS + " STRING,"
            + KEY_URL + " TEXT,"
            + KEY_SURVEY_START + " INTEGER,"
            + KEY_SURVEY_END + " INTEGER,"
            + "UNIQUE (" + KEY_URL + ") ON CONFLICT REPLACE"
            + ")";

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_YEARS_TABLE);
        db.execSQL(CREATE_SPONSORS_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_PAPERS_TABLE);
        db.execSQL(CREATE_SURVEYS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1) {

            Map<Note, Pair<Event, Paper>> oldMap = new HashMap<>();

            List<Note> oldNotes = getOldNotes(db);
            Schedule schedule = ((App)context.getApplicationContext()).getOldSchedule();
            for (Note oldNote : oldNotes) {
                schedule.setDayIndex(oldNote.getDayId());
                schedule.setCurrentEventIndex(oldNote.getEventId());
                schedule.setPaperIndex(oldNote.getPaperId());
                Event oldEvent = schedule.getCurrentEvent();
                Paper oldPaper = schedule.getCurrentPaper();
                oldMap.put(oldNote, new Pair<Event, Paper>(oldEvent, oldPaper));
            }
            ((App) context.getApplicationContext()).setOldNotes(oldMap);

            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

            // Create tables again
            onCreate(db);
        }

        if (oldVersion == 2) {
            db.execSQL("ALTER TABLE " + TABLE_PAPERS
                    + " ADD COLUMN " + KEY_ORDER +
                    " INTEGER DEFAULT 0");
        }

        if (oldVersion == 3) {
            db.execSQL(CREATE_SURVEYS_TABLE);
        }
    }

    public void addYear(BYear year) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT_ID, year.getObjectId());
        values.put(KEY_NAME, year.getName());
        values.put(KEY_INFO, year.getInfo());
        values.put(KEY_WELCOME, year.getWelcome());

        String surveysString = year.getSurveys();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class,new MyDateTypeAdapter())
                .create();
        List<BSurvey> surveys = gson.fromJson(surveysString, new TypeToken<List<BSurvey>>(){}.getType());
        if (surveys != null) {
            addSurveys(db, year.getObjectId(), surveys);
        }

        // Inserting Row
        db.insert(TABLE_YEARS, null, values);
        db.close();
    }

    private void addSurveys(SQLiteDatabase db, String yearId, List<BSurvey> surveys) {
        for (BSurvey survey : surveys) {
            addSurvey(db, survey, yearId);
        }
    }

    private void addSurvey(SQLiteDatabase db, BSurvey survey, String yearId) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, survey.getTitle());
        values.put(KEY_DETAILS, survey.getDetails());
        values.put(KEY_URL, survey.getUrl());
        values.put(KEY_SURVEY_START, survey.getStart().getTime());
        values.put(KEY_SURVEY_END, survey.getEnd().getTime());
        values.put(KEY_YEAR_ID, yearId);

        // Inserting Row
        db.insert(TABLE_SURVEYS, null, values);
    }

    public void addSponsors(String yearId, List<BSponsor> sponsors) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (BSponsor sponsor: sponsors) {
            addSponsor(db, sponsor, yearId);
        }
        db.close();
    }

    public void addEvents(String yearId, List<BEvent> events) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (BEvent event: events) {
            addEvent(db, event, yearId);
        }
        db.close();
    }

    public void addPapers(String eventId, List<BPaper> papers) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (BPaper paper: papers) {
            addPaper(db, paper, eventId);
        }
        db.close();
    }

    private void addSponsor(SQLiteDatabase db, BSponsor sponsor, String yearId) {
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT_ID, sponsor.getObjectId());
        values.put(KEY_URL, sponsor.getUrl());
        values.put(KEY_IMAGE_URL, sponsor.getImageUrl());
        values.put(KEY_YEAR_ID, yearId);

        // Inserting Row
        db.insert(TABLE_SPONSORS, null, values);
    }

    private void addEvent(SQLiteDatabase db, BEvent event, String yearId) {
        ContentValues values = new ContentValues();

        values.put(KEY_OBJECT_ID, event.getObjectId());
        values.put(KEY_YEAR_ID, yearId);
        values.put(KEY_DETAILS, event.getDetails());
        if (event.getEndDate() != null) {
            values.put(KEY_END_DATE, event.getEndDate().getTime());
        }
        values.put(KEY_PLACE, event.getLocation());
        values.put(KEY_START_DATE, event.getStartDate().getTime());
        values.put(KEY_SUBTITLE, event.getSubtitle());
        values.put(KEY_TITLE, event.getTitle());

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
    }

    private void addPaper(SQLiteDatabase db, BPaper paper, String eventId) {
        ContentValues values = new ContentValues();

        values.put(KEY_OBJECT_ID, paper.getObjectId());
        values.put(KEY_EVENT_ID, eventId);
        values.put(KEY_TITLE, paper.getTitle());
        values.put(KEY_AUTHOR, paper.getAuthor());
        values.put(KEY_SYNOPSIS, paper.getSynopsis());
        values.put(KEY_ORDER, paper.getOrder());

        // Inserting Row
        db.insert(TABLE_PAPERS, null, values);
    }

    public void addNoteSafe(BNote remoteNote) {
        BNote prevNote = getNoteBy(remoteNote.getEventId(), remoteNote.getPaperId());
        if (prevNote != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.updateWithOnConflict(TABLE_NOTES, getNoteContentValues(remoteNote),
                    KEY_EVENT_ID + "=? AND " + KEY_PAPER_ID + "=?",
                    new String[]{remoteNote.getEventId(), remoteNote.getPaperId()},
                    SQLiteDatabase.CONFLICT_REPLACE);
        } else {
            addNote(remoteNote);
        }
    }

    private ContentValues getNoteContentValues(BNote note) {
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT_ID, note.getObjectId());
        values.put(KEY_EVENT_ID, note.getEventId());
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_PAPER_ID, note.getPaperId());
        if (note.getCreated() != null) {
            values.put(KEY_CREATED, note.getCreated().getTime());
        }
        if (note.getUpdated() != null) {
            values.put(KEY_UPDATED, note.getUpdated().getTime());
        }
        return values;
    }

    public void addNote(BNote note) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_NOTES, null, getNoteContentValues(note));
    }

    private BNote constructNote(Cursor cursor) {

        BNote note = new BNote();
        note.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
        note.setEventId(cursor.getString(cursor.getColumnIndex(KEY_EVENT_ID)));
        note.setPaperId(cursor.getString(cursor.getColumnIndex(KEY_PAPER_ID)));
        note.setContent(cursor.getString(cursor.getColumnIndex(KEY_NOTE_CONTENT)));
        note.setCreated(new Date(cursor.getLong(cursor.getColumnIndex(KEY_CREATED))));
        note.setUpdated(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED))));
        return note;
    }

    public BNote getNoteBy(String eventId, String paperId) {
        if (eventId == null) {
            eventId = "null";
        }
        if (paperId == null) {
            paperId = "null";
        }
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, null,
                KEY_PAPER_ID + "=? AND " + KEY_EVENT_ID + "=?",
                new String[] {eventId, paperId}, null, null, KEY_CREATED, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return constructNote(cursor);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Note> getOldNotes(SQLiteDatabase db) {
        List<Note> noteList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                int dayId = cursor.getInt(cursor.getColumnIndex(KEY_DAY_INDEX));
                int eventId = cursor.getInt(cursor.getColumnIndex(KEY_EVENT_ID));
                int paperId = cursor.getInt(cursor.getColumnIndex(KEY_PAPER_ID));
                String content = cursor.getString(cursor.getColumnIndex(KEY_NOTE_CONTENT));
                String eventName = cursor.getString(cursor.getColumnIndex(KEY_EVENT_TITLE));;
                Note note = new Note(id, dayId, eventId, paperId, content, eventName);
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return noteList;
    }

    public List<BYear> getAllYears() {
        List<BYear> yearList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_YEARS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                yearList.add(constructYear(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return yearList;
    }

    public List<String> getAlYearNames() {
        List<String> names = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_YEARS, new String[] {KEY_NAME}, null, null, null, null, KEY_NAME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return names;
    }

    public Survey getLatestSurvey() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_YEARS,
                new String[] {KEY_SURVEY_URL, KEY_SURVEY_START, KEY_SURVEY_END},
                null, null, null, null, KEY_NAME + " DESC", "1");
        Survey survey = null;
        if (cursor.moveToFirst()) {
            survey = new Survey();
            survey.setSurveyUrl(cursor.getString(cursor.getColumnIndex(KEY_SURVEY_URL)));
            long endMillis = cursor.getLong(cursor.getColumnIndex(KEY_SURVEY_END));
            if (endMillis != 0) {
                survey.setSurveyEnd(new Date(endMillis));
            }
            long startMillis = cursor.getLong(cursor.getColumnIndex(KEY_SURVEY_START));
            if (endMillis != 0) {
                survey.setSurveyStart(new Date(startMillis));
            }
        }
        cursor.close();
        db.close();
        return survey;
    }

    public BYear getLastYear() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_YEARS, null, null,
                null, null, null, KEY_NAME + " DESC", "1");

        BYear year = null;
        if (cursor.moveToFirst()) {
            year = constructYear(cursor);
        }
        cursor.close();
        db.close();
        return year;
    }

    public BYear getYearByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_YEARS, null, KEY_NAME + "=?",
                new String[] {name}, null, null, KEY_NAME + " DESC", "1");

        BYear year = null;
        if (cursor.moveToFirst()) {
            year = constructYear(cursor);
        }
        cursor.close();
        db.close();
        return year;
    }

    private BYear constructYear(Cursor cursor) {
        BYear year = new BYear();
        year.setName(cursor.getInt(cursor.getColumnIndex(KEY_NAME)));
        year.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
        year.setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));
        year.setWelcome(cursor.getString(cursor.getColumnIndex(KEY_WELCOME)));
        List<BSponsor> sponsors = getAllSponsorsFor(year.getObjectId());
        year.setSponsors(sponsors);
        List<BEvent> events = getAllEventsFor(year.getObjectId());
        year.setEvents(events);
        return year;
    }

    public List<BSurvey> getSurveys(String yearId) {
        List<BSurvey> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SURVEYS + " WHERE (" + KEY_YEAR_ID + " == '" + yearId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BSurvey survey = new BSurvey();
                survey.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                survey.setDetails(cursor.getString(cursor.getColumnIndex(KEY_DETAILS)));
                survey.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
                long start = cursor.getLong(cursor.getColumnIndex(KEY_SURVEY_START));
                long end = cursor.getLong(cursor.getColumnIndex(KEY_SURVEY_END));
                survey.setStart(new Date(start));
                survey.setEnd(new Date(end));
                list.add(survey);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return list;
    }

    public List<BSponsor> getAllSponsors() {
        List<BSponsor> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SPONSORS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BSponsor sponsor = new BSponsor();
                sponsor.setImageUrl(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));
                sponsor.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
                sponsor.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
                list.add(sponsor);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return list;
    }

    private List<BSponsor> getAllSponsorsFor(String yearId) {
        List<BSponsor> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SPONSORS + " WHERE (" + KEY_YEAR_ID + " == '" + yearId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BSponsor sponsor = new BSponsor();
                sponsor.setImageUrl(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));
                sponsor.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
                sponsor.setUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
                list.add(sponsor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return list;
    }

    public BEvent getMatchedEvent(Date eventDate, Event oldEvent) {
        String timeString = oldEvent.getTime();
        String[] parts = timeString.split("-");
        String startString = parts[0].trim();
        String[] timeParts = startString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1].substring(0,2));
        boolean isPm = "pm".equals(timeParts[1].substring(2,4).toLowerCase());
        if (isPm) {
            hours += 12;
        }

        TimeZone utc = TimeZone.getTimeZone("UTC");

        Calendar calendar = Calendar.getInstance(utc);
        calendar.setTime(eventDate);
        calendar.set(Calendar.HOUR, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, KEY_DETAILS + "=? AND " + KEY_TITLE + "=? AND " + KEY_START_DATE + "=?",
                new String[] {oldEvent.getDetails(), oldEvent.getTitle(), String.valueOf(calendar.getTimeInMillis())}, null, null, null);

        BEvent event = null;
        if (cursor.moveToFirst()) {
            event = constructEvent(cursor);
        }
        cursor.close();
        db.close();
        return event;
    }

    public BPaper getMatchedPaper(Paper oldPaper) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_PAPERS, null, KEY_SYNOPSIS + "=?",
                new String[] {oldPaper.getSynopsis()}, null, null, null);

        BPaper paper = null;
        if (cursor.moveToFirst()) {
            paper = constructPaper(cursor);
        }
        cursor.close();
        db.close();
        return paper;
    }

    public BEvent getEventById(String objectId) {

        if (TextUtils.isEmpty(objectId)) {
            return null;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE (" + KEY_OBJECT_ID + " == '" + objectId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                BEvent event = constructEvent(cursor);
                cursor.close();
                return event;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        } else {
            return null;
        }
    }

    public List<BEvent> getAllEventsFor(long startMillis, long endMillis) {
        List<BEvent> list = new ArrayList<>();
        // Select All Query

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, KEY_START_DATE + ">=? and " + KEY_START_DATE + "<=?",
                new String[] { String.valueOf(startMillis), String.valueOf(endMillis) }, null, null, KEY_START_DATE);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(constructEvent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return list;
    }

    private List<BEvent> getAllEventsFor(String yearId) {
        List<BEvent> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, KEY_YEAR_ID + "=?",
                new String[] {yearId}, null, null, KEY_START_DATE);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(constructEvent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return list;
    }

    @Nullable
    public BEvent getEventBefore(long startMillis) {
        return getSeqEvent("<", KEY_START_DATE + " DESC", startMillis);
    }

    @Nullable
    public BEvent getEventAfter(long startMillis) {
        return getSeqEvent(">", KEY_START_DATE, startMillis);
    }

    private BEvent getSeqEvent(String direction, String orderBy, long startMillis) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, KEY_START_DATE + direction + "?",
                new String[] {String.valueOf(startMillis)}, null, null, orderBy, "1");

        BEvent event = null;
        if (cursor.moveToFirst()) {
            event = constructEvent(cursor);
        }
        cursor.close();
        db.close();
        return event;
    }

    private BEvent constructEvent(Cursor cursor) {
        BEvent event = new BEvent();
        event.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
        event.setDetails(cursor.getString(cursor.getColumnIndex(KEY_DETAILS)));
        long endMillis = cursor.getLong(cursor.getColumnIndex(KEY_END_DATE));
        if (endMillis != 0) {
            event.setEndDate(new Date(endMillis));
        }
        event.setLocation(cursor.getString(cursor.getColumnIndex(KEY_PLACE)));
        event.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(KEY_START_DATE))));
        event.setSubtitle(cursor.getString(cursor.getColumnIndex(KEY_SUBTITLE)));
        event.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        event.setPapers(getAllPapersFor(event.getObjectId()));
        return event;
    }

    private List<BPaper> getAllPapersFor(String eventId) {
        List<BPaper> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_PAPERS, null, KEY_EVENT_ID + "=?", new String[]{eventId},null, null, KEY_ORDER);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                list.add(constructPaper(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return list;
    }

    public BPaper getPaperById(String objectId) {

        if (TextUtils.isEmpty(objectId)) {
            return null;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_PAPERS + " WHERE (" + KEY_OBJECT_ID + " == '" + objectId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                BPaper paper = constructPaper(cursor);
                cursor.close();
                return paper;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        } else {
            return null;
        }
    }

    private BPaper constructPaper(Cursor cursor) {
        BPaper paper = new BPaper();
        paper.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
        paper.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        paper.setAuthor(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR)));
        paper.setSynopsis(cursor.getString(cursor.getColumnIndex(KEY_SYNOPSIS)));
        paper.setOrder(cursor.getInt(cursor.getColumnIndex(KEY_ORDER)));
        return paper;
    }

    public List<BNote> getAllNotes() {
        List<BNote> noteList= new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        if (cursor.moveToFirst()) {
            do {
                BNote note = constructNote(cursor);
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return noteList;
    }

    public void deleteNote(BNote note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_OBJECT_ID + " = ?",
                new String[] { note.getObjectId() });
        db.close();
    }
}

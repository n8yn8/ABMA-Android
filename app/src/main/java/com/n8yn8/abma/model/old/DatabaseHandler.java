package com.n8yn8.abma.model.old;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.backendless.BPaper;
import com.n8yn8.abma.model.backendless.BSponsor;
import com.n8yn8.abma.model.backendless.BYear;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nate on 3/14/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "abma";

    // Table names
    private static final String TABLE_YEARS = "years";
    private static final String TABLE_SPONSORS = "sponsors";
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_PAPERS = "papers";
    private static final String TABLE_NOTES = "notes";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_OBJECT_ID = "object_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_INFO = "info";
    private static final String KEY_WELCOME = "welcome";
    private static final String KEY_URL = "url";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_YEAR_ID = "year_id";
    private static final String KEY_DAY_INDEX = "day_index";
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
    private static final String KEY_NOTE_CONTENT = "note_content";
    private static final String KEY_EVENT_TITLE = "event_title";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private String CREATE_PAPERS_TABLE = "CREATE TABLE " + TABLE_PAPERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_EVENT_ID + " STRING,"
            + KEY_TITLE + " STRING,"
            + KEY_AUTHOR + " STRING,"
            + KEY_SYNOPSIS + " TEXT,"
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
            + KEY_DAY_INDEX + " INTEGER,"
            + KEY_EVENT_ID + " INTEGER,"
            + KEY_PAPER_ID + " INTEGER,"
            + KEY_NOTE_CONTENT + " TEXT,"
            + KEY_EVENT_TITLE + " TEXT"
            + ")";

    private String CREATE_YEARS_TABLE = "CREATE TABLE " + TABLE_YEARS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_OBJECT_ID + " STRING,"
            + KEY_NAME + " INTEGER,"
            + KEY_INFO + " TEXT,"
            + KEY_WELCOME + " TEXT,"
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

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_YEARS_TABLE);
        db.execSQL(CREATE_SPONSORS_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_PAPERS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        //TODO: migrate notes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        // Create tables again
        onCreate(db);
    }

    public void addYear(BYear year) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT_ID, year.getObjectId());
        values.put(KEY_NAME, year.getName());
        values.put(KEY_INFO, year.getInfo());
        values.put(KEY_WELCOME, year.getWelcome());

        for (BSponsor sponsor: year.getSponsors()) {
            addSponsor(sponsor, year.getObjectId());
        }

        for (BEvent event: year.getEvents()) {
            addEvent(event, year.getObjectId());
        }

        // Inserting Row
        db.insert(TABLE_YEARS, null, values);
        db.close();
    }

    private void addSponsor(BSponsor sponsor, String yearId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OBJECT_ID, sponsor.getObjectId());
        values.put(KEY_URL, sponsor.getUrl());
        values.put(KEY_IMAGE_URL, sponsor.getImageUrl());
        values.put(KEY_YEAR_ID, yearId);

        // Inserting Row
        db.insert(TABLE_SPONSORS, null, values);
//        db.close();
    }

    private void addEvent(BEvent event, String yearId) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        for (BPaper paper : event.getPapers()) {
            addPaper(paper, event.getObjectId());
        }

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
//        db.close(); // Closing database connection
    }

    private void addPaper(BPaper paper, String eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_OBJECT_ID, paper.getObjectId());
        values.put(KEY_EVENT_ID, eventId);
        values.put(KEY_TITLE, paper.getTitle());
        values.put(KEY_AUTHOR, paper.getAuthor());
        values.put(KEY_SYNOPSIS, paper.getSynopsis());

        // Inserting Row
        db.insert(TABLE_PAPERS, null, values);
//        db.close(); // Closing database connection
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY_INDEX, note.getDayId());
        values.put(KEY_EVENT_ID, note.getEventId());
        values.put(KEY_NOTE_CONTENT, note.getContent());
        values.put(KEY_EVENT_TITLE, note.getEventName());
        values.put(KEY_PAPER_ID, note.getPaperId());

        db.insert(TABLE_NOTES, null, values);
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {KEY_ID, KEY_DAY_INDEX, KEY_EVENT_ID, KEY_PAPER_ID, KEY_NOTE_CONTENT, KEY_EVENT_TITLE}, KEY_EVENT_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return new Note(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Note getNote(int dayId, int eventId, int paperId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {KEY_ID, KEY_DAY_INDEX, KEY_EVENT_ID, KEY_PAPER_ID, KEY_NOTE_CONTENT, KEY_EVENT_TITLE}, KEY_EVENT_ID + "=? AND " + KEY_PAPER_ID + "=? AND " + KEY_DAY_INDEX + "=?", new String[] {String.valueOf(eventId), String.valueOf(paperId), String.valueOf(dayId)}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return new Note(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
            } else {
                return null;
            }
        } else {
            return null;
        }
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
                BYear year = new BYear();
                year.setName(cursor.getInt(cursor.getColumnIndex(KEY_NAME)));
                year.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
                year.setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));
                year.setWelcome(cursor.getString(cursor.getColumnIndex(KEY_WELCOME)));
                List<BSponsor> sponsors = getAllSponsorsFor(year.getObjectId());
                year.setSponsors(sponsors);
                //TODO: set events
                yearList.add(year);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return yearList;
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

    public List<BSponsor> getAllSponsorsFor(String yearId) {
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

        // return contact list
        return list;
    }

    public List<BEvent> getAllEventsFor(String yearId) {
        List<BEvent> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE (" + KEY_YEAR_ID + " == '" + yearId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
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
                list.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return list;
    }

    public List<BPaper> getAllPapersFor(String eventId) {
        List<BPaper> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PAPERS + " WHERE (" + KEY_EVENT_ID + " == '" + eventId + "')";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BPaper paper = new BPaper();
                paper.setObjectId(cursor.getString(cursor.getColumnIndex(KEY_OBJECT_ID)));
                paper.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                paper.setAuthor(cursor.getString(cursor.getColumnIndex(KEY_AUTHOR)));
                paper.setSynopsis(cursor.getString(cursor.getColumnIndex(KEY_SYNOPSIS)));
                list.add(paper);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return list;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList= new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return noteList;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY_INDEX, note.getDayId());
        values.put(KEY_EVENT_ID, note.getEventId());
        values.put(KEY_PAPER_ID, note.getPaperId());
        values.put(KEY_NOTE_CONTENT, note.getContent());

        // updating row
        return db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        db.close();
    }
}

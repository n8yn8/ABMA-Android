package com.n8yn8.abma.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nate on 3/14/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "abma";

    // Contacts table name
    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_NOTES = "notes";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DAY_INDEX = "day_index";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_TIME = "time";
    private static final String KEY_PLACE = "place";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_PAPER_ID = "paper_id";
    private static final String KEY_NOTE_CONTENT = "note_content";
    private static final String KEY_EVENT_TITLE = "event_title";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DAY_INDEX + " INTEGER,"
                + KEY_TITLE + " TEXT,"
                + KEY_SUBTITLE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_PLACE + " TEXT,"
                + KEY_DETAILS + " TEXT"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DAY_INDEX + " INTEGER,"
                + KEY_EVENT_ID + " INTEGER,"
                + KEY_PAPER_ID + " INTEGER,"
                + KEY_NOTE_CONTENT + " TEXT,"
                + KEY_EVENT_TITLE + " TEXT"
                + ")";
        db.execSQL(CREATE_NOTES_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        // Create tables again
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAY_INDEX, event.getIndex());
        values.put(KEY_TITLE, event.getTitle());
        values.put(KEY_SUBTITLE, event.getSubtitle());
        values.put(KEY_TIME, event.getTime());
        values.put(KEY_PLACE, event.getPlace());
        values.put(KEY_DETAILS, event.getDetails());

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
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

    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID, KEY_EVENT_ID,
                        KEY_TITLE, KEY_SUBTITLE, KEY_TIME, KEY_PLACE, KEY_DETAILS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        return event;
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

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null); //TODO: sort

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        // return contact list
        return eventList;
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

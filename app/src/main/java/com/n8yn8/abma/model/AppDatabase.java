package com.n8yn8.abma.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.n8yn8.abma.model.dao.EventDao;
import com.n8yn8.abma.model.dao.MapDao;
import com.n8yn8.abma.model.dao.NoteDao;
import com.n8yn8.abma.model.dao.PaperDao;
import com.n8yn8.abma.model.dao.SurveyDao;
import com.n8yn8.abma.model.dao.YearDao;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Map;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.entities.Survey;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.model.old.DatabaseHandler;

@Database(entities = {Year.class, Event.class, Paper.class, Sponsor.class, Map.class, Note.class, Survey.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract YearDao yearDao();

    public abstract EventDao eventDao();

    public abstract PaperDao paperDao();

    public abstract MapDao mapDao();

    public abstract NoteDao noteDao();

    public abstract SurveyDao surveyDao();

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //Years
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_YEARS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_OBJECT_ID + " TEXT,"
                    + DatabaseHandler.KEY_NAME + " INTEGER,"
                    + DatabaseHandler.KEY_INFO + " TEXT,"
                    + DatabaseHandler.KEY_WELCOME + " TEXT,"
                    + "UNIQUE (" + DatabaseHandler.KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
                    + ")");

            database.execSQL(
                    "INSERT INTO years_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_NAME + ", "
                            + DatabaseHandler.KEY_INFO + ", "
                            + DatabaseHandler.KEY_WELCOME +
                            ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_NAME + ", "
                            + DatabaseHandler.KEY_INFO + ", "
                            + DatabaseHandler.KEY_WELCOME +
                            " FROM years");

            database.execSQL("DROP TABLE years");

            database.execSQL("ALTER TABLE years_new RENAME TO years");

            database.execSQL("CREATE UNIQUE INDEX index_years_object_id ON years(object_id)");

            //Events
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_EVENTS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_OBJECT_ID + " TEXT,"
                    + DatabaseHandler.KEY_YEAR_ID + " TEXT,"
                    + DatabaseHandler.KEY_DETAILS + " TEXT,"
                    + DatabaseHandler.KEY_END_DATE + " INTEGER,"
                    + DatabaseHandler.KEY_START_DATE + " INTEGER,"
                    + DatabaseHandler.KEY_PLACE + " TEXT,"
                    + DatabaseHandler.KEY_TITLE + " TEXT,"
                    + DatabaseHandler.KEY_SUBTITLE + " TEXT,"
                    + "UNIQUE (" + DatabaseHandler.KEY_OBJECT_ID + ") ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO events_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_DETAILS + ", "
                            + DatabaseHandler.KEY_END_DATE + ", "
                            + DatabaseHandler.KEY_START_DATE + ", "
                            + DatabaseHandler.KEY_PLACE + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_SUBTITLE +
                            ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_DETAILS + ", "
                            + DatabaseHandler.KEY_END_DATE + ", "
                            + DatabaseHandler.KEY_START_DATE + ", "
                            + DatabaseHandler.KEY_PLACE + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_SUBTITLE +
                            " FROM events");

            database.execSQL("DROP TABLE events");

            database.execSQL("ALTER TABLE events_new RENAME TO events");

            database.execSQL("CREATE UNIQUE INDEX index_events_object_id ON events(object_id)");
            database.execSQL("CREATE INDEX index_events_year_id ON events(year_id)");

            //Papers
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_PAPERS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_OBJECT_ID + " TEXT,"
                    + DatabaseHandler.KEY_EVENT_ID + " TEXT,"
                    + DatabaseHandler.KEY_TITLE + " TEXT,"
                    + DatabaseHandler.KEY_AUTHOR + " TEXT,"
                    + DatabaseHandler.KEY_SYNOPSIS + " TEXT,"
                    + DatabaseHandler.KEY_ORDER + " INTEGER,"
                    + "UNIQUE (" + DatabaseHandler.KEY_OBJECT_ID + ") ON CONFLICT REPLACE, FOREIGN KEY(`event_id`) REFERENCES `events`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO papers_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_EVENT_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_AUTHOR + ", "
                            + DatabaseHandler.KEY_SYNOPSIS + ", "
                            + DatabaseHandler.KEY_ORDER
                            + ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_EVENT_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_AUTHOR + ", "
                            + DatabaseHandler.KEY_SYNOPSIS + ", "
                            + DatabaseHandler.KEY_ORDER
                            + " FROM papers");

            database.execSQL("DROP TABLE papers");

            database.execSQL("ALTER TABLE papers_new RENAME TO papers");

            database.execSQL("CREATE UNIQUE INDEX index_papers_object_id ON papers(object_id)");
            database.execSQL("CREATE INDEX index_papers_event_id ON papers(event_id)");

            //Sponsors
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_SPONSORS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_OBJECT_ID + " TEXT,"
                    + DatabaseHandler.KEY_YEAR_ID + " TEXT,"
                    + DatabaseHandler.KEY_IMAGE_URL + " TEXT,"
                    + DatabaseHandler.KEY_URL + " TEXT,"
                    + "UNIQUE (" + DatabaseHandler.KEY_OBJECT_ID + ") ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO sponsors_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_IMAGE_URL + ", "
                            + DatabaseHandler.KEY_URL
                            + ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_IMAGE_URL + ", "
                            + DatabaseHandler.KEY_URL
                            + " FROM sponsors");

            database.execSQL("DROP TABLE sponsors");

            database.execSQL("ALTER TABLE sponsors_new RENAME TO sponsors");

            database.execSQL("CREATE UNIQUE INDEX index_sponsors_object_id ON sponsors(object_id)");
            database.execSQL("CREATE INDEX index_sponsors_year_id ON sponsors(year_id)");

            //Maps
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_MAPS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_YEAR_ID + " TEXT,"
                    + DatabaseHandler.KEY_TITLE + " TEXT,"
                    + DatabaseHandler.KEY_URL + " TEXT,"
                    + "UNIQUE (" + DatabaseHandler.KEY_URL + ") ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO maps_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_URL
                            + ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_URL
                            + " FROM maps");

            database.execSQL("DROP TABLE maps");

            database.execSQL("ALTER TABLE maps_new RENAME TO maps");

            database.execSQL("CREATE UNIQUE INDEX index_maps_url ON maps(url)");
            database.execSQL("CREATE INDEX index_maps_year_id ON maps(year_id)");

            //Note
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_NOTES + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_OBJECT_ID + " TEXT,"
                    + DatabaseHandler.KEY_EVENT_ID + " TEXT,"
                    + DatabaseHandler.KEY_PAPER_ID + " TEXT,"
                    + DatabaseHandler.KEY_NOTE_CONTENT + " TEXT,"
                    + DatabaseHandler.KEY_CREATED + " INTEGER,"
                    + DatabaseHandler.KEY_UPDATED + " INTEGER,"
                    + "UNIQUE (" + DatabaseHandler.KEY_OBJECT_ID + ") ON CONFLICT REPLACE, FOREIGN KEY(`event_id`) REFERENCES `events`(`object_id`), FOREIGN KEY(`paper_id`) REFERENCES `papers`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO notes_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_EVENT_ID + ", "
                            + DatabaseHandler.KEY_PAPER_ID + ", "
                            + DatabaseHandler.KEY_NOTE_CONTENT + ", "
                            + DatabaseHandler.KEY_CREATED + ", "
                            + DatabaseHandler.KEY_UPDATED
                            + ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_OBJECT_ID + ", "
                            + DatabaseHandler.KEY_EVENT_ID + ", "
                            + DatabaseHandler.KEY_PAPER_ID + ", "
                            + DatabaseHandler.KEY_NOTE_CONTENT + ", "
                            + DatabaseHandler.KEY_CREATED + ", "
                            + DatabaseHandler.KEY_UPDATED
                            + " FROM notes");

            database.execSQL("DROP TABLE notes");

            database.execSQL("ALTER TABLE notes_new RENAME TO notes");

            database.execSQL("CREATE UNIQUE INDEX index_notes_object_id ON notes(object_id)");
            database.execSQL("CREATE INDEX index_notes_event_id ON notes(event_id)");
            database.execSQL("CREATE INDEX index_notes_paper_id ON notes(paper_id)");

            //Surveys
            database.execSQL("CREATE TABLE " + DatabaseHandler.TABLE_SURVEYS + "_new ("
                    + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseHandler.KEY_YEAR_ID + " TEXT,"
                    + DatabaseHandler.KEY_TITLE + " TEXT,"
                    + DatabaseHandler.KEY_DETAILS + " TEXT,"
                    + DatabaseHandler.KEY_URL + " TEXT,"
                    + DatabaseHandler.KEY_SURVEY_START + " INTEGER,"
                    + DatabaseHandler.KEY_SURVEY_END + " INTEGER,"
                    + "UNIQUE (" + DatabaseHandler.KEY_URL + ") ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO surveys_new ("
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_DETAILS + ", "
                            + DatabaseHandler.KEY_URL + ", "
                            + DatabaseHandler.KEY_SURVEY_START + ", "
                            + DatabaseHandler.KEY_SURVEY_END
                            + ") SELECT "
                            + DatabaseHandler.KEY_ID + ", "
                            + DatabaseHandler.KEY_YEAR_ID + ", "
                            + DatabaseHandler.KEY_TITLE + ", "
                            + DatabaseHandler.KEY_DETAILS + ", "
                            + DatabaseHandler.KEY_URL + ", "
                            + DatabaseHandler.KEY_SURVEY_START + ", "
                            + DatabaseHandler.KEY_SURVEY_END
                            + " FROM surveys");

            database.execSQL("DROP TABLE surveys");

            database.execSQL("ALTER TABLE surveys_new RENAME TO surveys");

            database.execSQL("CREATE UNIQUE INDEX index_surveys_url ON surveys(url)");
            database.execSQL("CREATE INDEX index_surveys_year_id ON surveys(year_id)");
        }
    };


    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DatabaseHandler.DATABASE_NAME)
                    .addMigrations(MIGRATION_5_6)
                    .build();
        }
        return INSTANCE;
    }
}

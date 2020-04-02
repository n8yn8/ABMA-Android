package com.n8yn8.abma.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.n8yn8.abma.model.dao.EventDao;
import com.n8yn8.abma.model.dao.MapDao;
import com.n8yn8.abma.model.dao.NoteDao;
import com.n8yn8.abma.model.dao.PaperDao;
import com.n8yn8.abma.model.dao.SponsorDao;
import com.n8yn8.abma.model.dao.SurveyDao;
import com.n8yn8.abma.model.dao.YearDao;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Map;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.model.entities.Sponsor;
import com.n8yn8.abma.model.entities.Survey;
import com.n8yn8.abma.model.entities.Year;

@Database(entities = {Year.class, Event.class, Paper.class, Sponsor.class, Map.class, Note.class, Survey.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            //Years
            database.execSQL("CREATE TABLE years_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "object_id TEXT,"
                    + "name INTEGER,"
                    + "info TEXT,"
                    + "welcome TEXT,"
                    + "UNIQUE (object_id) ON CONFLICT REPLACE"
                    + ")");

            database.execSQL(
                    "INSERT INTO years_new ("
                            + "id, "
                            + "object_id, "
                            + "name, "
                            + "info, "
                            + "welcome" +
                            ") SELECT "
                            + "id, "
                            + "object_id, "
                            + "name, "
                            + "info, "
                            + "welcome" +
                            " FROM years");

            database.execSQL("DROP TABLE years");

            database.execSQL("ALTER TABLE years_new RENAME TO years");

            database.execSQL("CREATE UNIQUE INDEX index_years_object_id ON years(object_id)");

            //Events
            database.execSQL("CREATE TABLE events_new ("
                    + "id INTEGER PRIMARY KEY, "
                    + "object_id TEXT, "
                    + "year_id TEXT, "
                    + "details TEXT, "
                    + "end_date INTEGER, "
                    + "start_date INTEGER, "
                    + "place TEXT, "
                    + "title TEXT,"
                    + "subtitle TEXT, "
                    + "UNIQUE (object_id) ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO events_new ("
                            + "id, "
                            + "object_id, "
                            + "year_id, "
                            + "details, "
                            + "end_date, "
                            + "start_date, "
                            + "place, "
                            + "title, "
                            + "subtitle" +
                            ") SELECT "
                            + "id, "
                            + "object_id, "
                            + "year_id, "
                            + "details, "
                            + "end_date, "
                            + "start_date, "
                            + "place, "
                            + "title, "
                            + "subtitle" +
                            " FROM events");

            database.execSQL("DROP TABLE events");

            database.execSQL("ALTER TABLE events_new RENAME TO events");

            database.execSQL("CREATE UNIQUE INDEX index_events_object_id ON events(object_id)");
            database.execSQL("CREATE INDEX index_events_year_id ON events(year_id)");

            //Papers
            database.execSQL("CREATE TABLE papers_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "object_id TEXT,"
                    + "event_id TEXT,"
                    + "title TEXT,"
                    + "author TEXT,"
                    + "synopsis TEXT,"
                    + "order_by INTEGER,"
                    + "UNIQUE (object_id) ON CONFLICT REPLACE, FOREIGN KEY(`event_id`) REFERENCES `events`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO papers_new ("
                            + "id, "
                            + "object_id, "
                            + "event_id, "
                            + "title, "
                            + "author, "
                            + "synopsis, "
                            + "order_by"
                            + ") SELECT "
                            + "id, "
                            + "object_id, "
                            + "event_id, "
                            + "title, "
                            + "author, "
                            + "synopsis, "
                            + "order_by"
                            + " FROM papers");

            database.execSQL("DROP TABLE papers");

            database.execSQL("ALTER TABLE papers_new RENAME TO papers");

            database.execSQL("CREATE UNIQUE INDEX index_papers_object_id ON papers(object_id)");
            database.execSQL("CREATE INDEX index_papers_event_id ON papers(event_id)");

            //Sponsors
            database.execSQL("CREATE TABLE sponsors_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "object_id TEXT,"
                    + "year_id TEXT,"
                    + "image_url TEXT,"
                    + "url TEXT,"
                    + "UNIQUE (object_id) ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO sponsors_new ("
                            + "id, "
                            + "object_id, "
                            + "year_id, "
                            + "image_url, "
                            + "url"
                            + ") SELECT "
                            + "id, "
                            + "object_id, "
                            + "year_id, "
                            + "image_url, "
                            + "url"
                            + " FROM sponsors");

            database.execSQL("DROP TABLE sponsors");

            database.execSQL("ALTER TABLE sponsors_new RENAME TO sponsors");

            database.execSQL("CREATE UNIQUE INDEX index_sponsors_object_id ON sponsors(object_id)");
            database.execSQL("CREATE INDEX index_sponsors_year_id ON sponsors(year_id)");

            //Maps
            database.execSQL("CREATE TABLE maps_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "year_id TEXT,"
                    + "title TEXT,"
                    + "url TEXT,"
                    + "UNIQUE (url) ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO maps_new ("
                            + "id, "
                            + "year_id, "
                            + "title, "
                            + "url"
                            + ") SELECT "
                            + "id, "
                            + "year_id, "
                            + "title, "
                            + "url"
                            + " FROM maps");

            database.execSQL("DROP TABLE maps");

            database.execSQL("ALTER TABLE maps_new RENAME TO maps");

            database.execSQL("CREATE UNIQUE INDEX index_maps_url ON maps(url)");
            database.execSQL("CREATE INDEX index_maps_year_id ON maps(year_id)");

            //Note
            database.execSQL("CREATE TABLE notes_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "object_id TEXT,"
                    + "event_id TEXT,"
                    + "paper_id TEXT,"
                    + "note_content TEXT,"
                    + "created_at INTEGER,"
                    + "updated_at INTEGER,"
                    + "UNIQUE (object_id) ON CONFLICT REPLACE, FOREIGN KEY(`event_id`) REFERENCES `events`(`object_id`), FOREIGN KEY(`paper_id`) REFERENCES `papers`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO notes_new ("
                            + "id, "
                            + "object_id, "
                            + "event_id, "
                            + "paper_id, "
                            + "note_content, "
                            + "created_at, "
                            + "updated_at"
                            + ") SELECT "
                            + "id, "
                            + "object_id, "
                            + "event_id, "
                            + "paper_id, "
                            + "note_content, "
                            + "created_at, "
                            + "updated_at"
                            + " FROM notes");

            database.execSQL("DROP TABLE notes");

            database.execSQL("ALTER TABLE notes_new RENAME TO notes");

            database.execSQL("CREATE UNIQUE INDEX index_notes_object_id ON notes(object_id)");
            database.execSQL("CREATE INDEX index_notes_event_id ON notes(event_id)");
            database.execSQL("CREATE INDEX index_notes_paper_id ON notes(paper_id)");

            //Surveys
            database.execSQL("CREATE TABLE surveys_new ("
                    + "id INTEGER PRIMARY KEY,"
                    + "year_id TEXT,"
                    + "title TEXT,"
                    + "details TEXT,"
                    + "url TEXT,"
                    + "survey_start INTEGER,"
                    + "survey_end INTEGER,"
                    + "UNIQUE (url) ON CONFLICT REPLACE, FOREIGN KEY(`year_id`) REFERENCES `years`(`object_id`)"
                    + ")");

            database.execSQL(
                    "INSERT INTO surveys_new ("
                            + "id, "
                            + "year_id, "
                            + "title, "
                            + "details, "
                            + "url, "
                            + "survey_start, "
                            + "survey_end"
                            + ") SELECT "
                            + "id, "
                            + "year_id, "
                            + "title, "
                            + "details, "
                            + "url, "
                            + "survey_start, "
                            + "survey_end"
                            + " FROM surveys");

            database.execSQL("DROP TABLE surveys");

            database.execSQL("ALTER TABLE surveys_new RENAME TO surveys");

            database.execSQL("CREATE UNIQUE INDEX index_surveys_url ON surveys(url)");
            database.execSQL("CREATE INDEX index_surveys_year_id ON surveys(year_id)");
        }
    };
    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "abma")
                    .addMigrations(MIGRATION_5_6)
                    .build();
        }
        return INSTANCE;
    }

    public abstract YearDao yearDao();

    public abstract EventDao eventDao();

    public abstract PaperDao paperDao();

    public abstract SponsorDao sponsorDao();

    public abstract MapDao mapDao();

    public abstract NoteDao noteDao();

    public abstract SurveyDao surveyDao();
}

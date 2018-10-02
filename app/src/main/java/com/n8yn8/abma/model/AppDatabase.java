package com.n8yn8.abma.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.n8yn8.abma.model.dao.EventDao;
import com.n8yn8.abma.model.dao.YearDao;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.model.old.DatabaseHandler;

@Database(entities = {Year.class, Event.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract YearDao yearDao();

    public abstract EventDao eventDao();

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
            database.execSQL("CREATE  INDEX index_events_year_id ON events(year_id)");
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

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

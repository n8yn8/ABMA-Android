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
            //No table alterations, only migrating from SQL to Room
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

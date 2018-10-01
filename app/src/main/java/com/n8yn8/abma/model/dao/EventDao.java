package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_EVENTS)
    List<Event> getEvents();

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_EVENTS
            + " WHERE " + DatabaseHandler.KEY_YEAR_ID + "=:yearId")
    List<Event> getEvents(final int yearId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Event event);
}

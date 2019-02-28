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

    @Query("SELECT * FROM events WHERE " + DatabaseHandler.KEY_YEAR_ID + "=:yearId ORDER BY start_date ASC")
    List<Event> getEvents(final String yearId);

    @Query("SELECT * FROM events WHERE start_date >= :startDate AND start_date <= :endDate ORDER BY start_date ASC")
    List<Event> getAllEventsFor(long startDate, long endDate);

    @Query("SELECT * FROM events WHERE object_id = :objectId LIMIT 1")
    Event getEventById(String objectId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Event> event);

    @Query("SELECT * FROM events WHERE start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    Event getEventBefore(long startDate);

    @Query("SELECT * FROM events WHERE year_id=:yearId AND start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    Event getEventBefore(String yearId, long startDate);

    @Query("SELECT * FROM events WHERE start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    Event getEventAfter(long startDate);

    @Query("SELECT * FROM events WHERE year_id=:yearId AND start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    Event getEventAfter(String yearId, long startDate);
}

package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.n8yn8.abma.model.entities.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun events(): List<Event>

    @Query("SELECT * FROM events WHERE year_id = :yearId ORDER BY start_date ASC")
    suspend fun getEvents(yearId: String): List<Event>

    @Query("SELECT * FROM events WHERE year_id = :yearId ORDER BY start_date ASC")
    fun getEventsLive(yearId: String): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE start_date >= :startDate AND start_date <= :endDate ORDER BY start_date ASC")
    fun getAllEventsFor(startDate: Long, endDate: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE object_id = :objectId LIMIT 1")
    suspend fun getEventById(objectId: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: List<Event>)

    @Query("SELECT * FROM events WHERE start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    fun getEventBefore(startDate: Long): Event?

    @Query("SELECT * FROM events WHERE year_id = :yearId AND start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    fun getEventBefore(yearId: String, startDate: Long): Event?

    @Query("SELECT * FROM events WHERE start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    fun getEventAfter(startDate: Long): Event?

    @Query("SELECT * FROM events WHERE year_id = :yearId AND start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    fun getEventAfter(yearId: String, startDate: Long): Event?

    @Delete
    fun delete(vararg events: Event)
}
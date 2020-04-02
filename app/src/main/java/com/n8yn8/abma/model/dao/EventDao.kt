package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.EventPapers

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    suspend fun events(): List<Event>

    @Query("SELECT * FROM events WHERE year_id = :yearId ORDER BY start_date ASC")
    suspend fun getEvents(yearId: String): List<Event>

    @Query("SELECT * FROM events WHERE year_id = :yearId ORDER BY start_date ASC")
    fun getEventsLive(yearId: String): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE start_date >= :startDate AND start_date <= :endDate ORDER BY start_date ASC")
    fun getAllEventsFor(startDate: Long, endDate: Long): LiveData<List<Event>>

    @Transaction
    @Query("SELECT * FROM events WHERE object_id = :objectId LIMIT 1")
    suspend fun getEventById(objectId: String): EventPapers?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: List<Event>)

    @Transaction
    @Query("SELECT * FROM events WHERE start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    suspend fun getEventBefore(startDate: Long): EventPapers?

    @Query("SELECT * FROM events WHERE year_id = :yearId AND start_date < :startDate ORDER BY start_date DESC LIMIT 1")
    suspend fun getEventBefore(yearId: String, startDate: Long): Event?

    @Transaction
    @Query("SELECT * FROM events WHERE start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    suspend fun getEventAfter(startDate: Long): EventPapers?

    @Query("SELECT * FROM events WHERE year_id = :yearId AND start_date > :startDate ORDER BY start_date ASC LIMIT 1")
    suspend fun getEventAfter(yearId: String, startDate: Long): Event?

    @Delete
    fun delete(vararg events: Event)
}
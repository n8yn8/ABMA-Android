package com.n8yn8.abma.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Paper

@Dao
interface PaperDao {
    @Query("SELECT * FROM papers WHERE event_id = :eventId ORDER BY order_by ASC")
    suspend fun getPapers(eventId: String): List<Paper>

    @Query("SELECT * FROM papers WHERE object_id = :objectId LIMIT 1")
    fun getPaperById(objectId: String): Paper?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paper: Paper)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paper: List<Paper>)
}
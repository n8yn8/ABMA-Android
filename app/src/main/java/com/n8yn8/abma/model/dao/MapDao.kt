package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Map

@Dao
interface MapDao {
    @Query("SELECT * FROM maps WHERE year_id = :yearId")
    fun getMaps(yearId: String): LiveData<List<Map>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(map: Map)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(maps: List<Map>)
}
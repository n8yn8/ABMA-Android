package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Year

@Dao
interface YearDao {
    @Query("SELECT * FROM years")
    suspend fun years(): List<Year>

    @Query("SELECT name FROM years ORDER BY name DESC")
    suspend fun allYearNames(): List<Int>

    @Query("SELECT * FROM years WHERE name = (SELECT MAX(name) FROM years) LIMIT 1")
    suspend fun lastYear(): Year?

    @get:Query("SELECT * FROM years WHERE name = (SELECT MAX(name) FROM years) LIMIT 1")
    val lastYearLive: LiveData<Year?>

    @Query("SELECT * FROM years WHERE name = :name LIMIT 1")
    suspend fun getYearByName(name: String): Year?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(year: Year)
}
package com.n8yn8.abma.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Year

@Dao
interface YearDao {
    @get:Query("SELECT * FROM years")
    val years: List<Year>

    @get:Query("SELECT name FROM years ORDER BY name DESC")
    val allYearNames: List<Int>

    @get:Query("SELECT * FROM years WHERE name = (SELECT MAX(name) FROM years) LIMIT 1")
    val lastYear: Year?

    @Query("SELECT * FROM years WHERE name = :name LIMIT 1")
    fun getYearByName(name: String): Year?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(year: Year)
}
package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n8yn8.abma.model.entities.Survey

@Dao
interface SurveyDao {
    @Query("SELECT * FROM surveys WHERE year_id = :yearId")
    fun getSurveys(yearId: String): LiveData<List<Survey>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(survey: Survey)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(surveys: List<Survey>)
}
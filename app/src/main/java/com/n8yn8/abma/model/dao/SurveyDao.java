package com.n8yn8.abma.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.n8yn8.abma.model.entities.Survey;

import java.util.List;

@Dao
public interface SurveyDao {

    @Query("SELECT * FROM surveys WHERE year_id = :yearId")
    List<Survey> getSurveys(final String yearId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Survey survey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Survey> surveys);
}

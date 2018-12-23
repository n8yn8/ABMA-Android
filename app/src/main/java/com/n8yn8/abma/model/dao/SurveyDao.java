package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Survey;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface SurveyDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_SURVEYS
            + " WHERE " + DatabaseHandler.KEY_YEAR_ID + "=:yearId")
    List<Survey> getSurveys(final String yearId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Survey survey);
}

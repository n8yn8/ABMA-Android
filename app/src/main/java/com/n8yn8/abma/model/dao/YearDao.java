package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Year;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface YearDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_YEARS)
    List<Year> getYears();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Year year);

}

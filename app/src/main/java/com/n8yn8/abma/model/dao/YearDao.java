package com.n8yn8.abma.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.n8yn8.abma.model.entities.Year;

import java.util.List;

@Dao
public interface YearDao {

    @Query("SELECT * FROM years")
    List<Year> getYears();

    @Query("SELECT name FROM years ORDER BY name DESC")
    List<Integer> getAllYearNames();

    @Query("SELECT * FROM years WHERE name = (SELECT MAX(name) FROM years) LIMIT 1")
    Year getLastYear();

    @Query("SELECT * FROM years WHERE name = :name LIMIT 1")
    Year getYearByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Year year);

}

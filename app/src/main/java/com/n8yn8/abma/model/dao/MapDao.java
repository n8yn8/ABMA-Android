package com.n8yn8.abma.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.n8yn8.abma.model.entities.Map;

import java.util.List;

@Dao
public interface MapDao {

    @Query("SELECT * FROM maps WHERE year_id = :yearId")
    List<Map> getMaps(final String yearId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Map map);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Map> maps);
}

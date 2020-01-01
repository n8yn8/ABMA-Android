package com.n8yn8.abma.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface PaperDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_PAPERS
            + " WHERE " + DatabaseHandler.KEY_EVENT_ID + "=:eventId ORDER BY order_by ASC")
    List<Paper> getPapers(final String eventId);

    @Query("SELECT * FROM papers WHERE object_id = :objectId LIMIT 1")
    Paper getPaperById(String objectId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Paper paper);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Paper> paper);
}

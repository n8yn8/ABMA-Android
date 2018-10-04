package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface PaperDao {

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_PAPERS
            + " WHERE " + DatabaseHandler.KEY_EVENT_ID + "=:eventId")
    List<Paper> getPapers(final String eventId);

    @Query("SELECT * FROM papers WHERE object_id = :objectId LIMIT 1")
    Paper getPaperById(String objectId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Paper paper);
}

package com.n8yn8.abma.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes")
    List<Note> getNotes();

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_NOTES
            + " WHERE " + DatabaseHandler.KEY_EVENT_ID + "=:eventId "
            + "AND " + DatabaseHandler.KEY_PAPER_ID + "=:paperId LIMIT 1")
    Note getNote(final String eventId, final String paperId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Note note);

    @Delete
    void delete(Note... notes);
}

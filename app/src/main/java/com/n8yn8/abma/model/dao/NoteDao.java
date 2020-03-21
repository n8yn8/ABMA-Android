package com.n8yn8.abma.model.dao;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.List;

@Dao
public abstract class NoteDao {

    @Query("SELECT * FROM notes")
    public abstract List<Note> getNotes();

    @Query("SELECT * FROM notes")
    public abstract LiveData<List<Note>> getNotesLive();

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_NOTES
            + " WHERE " + DatabaseHandler.KEY_EVENT_ID + "=:eventId"
            + " AND " + DatabaseHandler.KEY_PAPER_ID + " IS NULL LIMIT 1")
    @Nullable
    public abstract Note getNote(final String eventId);

    @Query("SELECT * FROM " + DatabaseHandler.TABLE_NOTES
            + " WHERE " + DatabaseHandler.KEY_EVENT_ID + "=:eventId"
            + " AND " + DatabaseHandler.KEY_PAPER_ID + "=:paperId LIMIT 1")
    @Nullable
    public abstract Note getNote(final String eventId, final String paperId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(List<Note> notes);

    @Transaction
    public void deleteInsert(Note deleteNote, Note insertNote) {
        delete(deleteNote);
        insert(insertNote);
    }

    @Delete
    public abstract void delete(Note... notes);
}

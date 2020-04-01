package com.n8yn8.abma.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.NoteEventPaper

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun notes(): List<Note>

    @get:Query("SELECT * FROM notes")
    @get:Transaction
    val notesLive: LiveData<List<NoteEventPaper>>

    @get:Query("SELECT * FROM notes")
    @get:Transaction
    val notesEventPaper: List<NoteEventPaper>

    @Query("SELECT * FROM notes WHERE event_id = :eventId AND paper_id IS NULL LIMIT 1")
    fun getNoteLive(eventId: String): LiveData<Note>

    @Query("SELECT * FROM notes WHERE event_id = :eventId AND paper_id IS NULL LIMIT 1")
    suspend fun getNote(eventId: String): Note?

    @Query("SELECT * FROM notes WHERE event_id = :eventId AND paper_id = :paperId LIMIT 1")
    fun getNoteLive(eventId: String, paperId: String): LiveData<Note>

    @Query("SELECT * FROM notes WHERE event_id = :eventId AND paper_id = :paperId LIMIT 1")
    suspend fun getNote(eventId: String, paperId: String): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: List<Note>)

    @Transaction
    suspend fun deleteInsert(deleteNote: Note, insertNote: Note) {
        delete(deleteNote)
        insert(insertNote)
    }

    @Delete
    suspend fun delete(notes: Note)
}
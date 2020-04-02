package com.n8yn8.abma.model.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.NoteEventPaper
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .setTransactionExecutor(Executors.newSingleThreadExecutor())
                .setQueryExecutor(Executors.newSingleThreadExecutor())
                .build()
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
            database.paperDao().insert(FakeData.getPaper())
        }
    }

    @Test
    fun insert() {
        val notes = database.noteDao().notesEventPaper
        Assert.assertEquals(emptyList<NoteEventPaper>(), notes)

        val localEventNote = FakeData.getNote(FakeData.getEvent().objectId)
        val localPaperNote = FakeData.getNote(FakeData.getEvent().objectId, FakeData.getPaper().objectId)
        runBlocking {
            database.noteDao().insert(localEventNote)
            database.noteDao().insert(localPaperNote)
            val saved = database.noteDao().notes()
            Assert.assertEquals(2, saved.size)
            val savedNotesEventPaper = database.noteDao().notesEventPaper
            Assert.assertEquals(2, savedNotesEventPaper.size)
        }

        val remoteEventNote = localEventNote.apply { objectId = "remoteEventNote" }
        val remotePaperNote = localPaperNote.apply { objectId = "remotePaperNote" }
        runBlocking {
            database.noteDao().insert(remoteEventNote)
            database.noteDao().insert(remotePaperNote)
        }

        runBlocking {
            val allNotes = database.noteDao().notes()
            Assert.assertEquals(4, allNotes.size)
            val savedNotesEventPaper = database.noteDao().notesEventPaper
            Assert.assertEquals(4, savedNotesEventPaper.size)
        }
    }

    @Test
    fun insertDelete() {
        val localEventNote = FakeData.getNote(FakeData.getEvent().objectId)
        val localPaperNote = FakeData.getNote(FakeData.getEvent().objectId, FakeData.getPaper().objectId)
        runBlocking {
            database.noteDao().insert(localEventNote)
            database.noteDao().insert(localPaperNote)
        }

        runBlocking {
            database.noteDao().deleteInsert(localEventNote, localEventNote)
        }
    }
}
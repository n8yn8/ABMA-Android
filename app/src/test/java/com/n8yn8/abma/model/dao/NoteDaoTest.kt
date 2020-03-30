package com.n8yn8.abma.model.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Mock
    lateinit var notesObserver: Observer<List<NoteEventPaper>>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
            database.paperDao().insert(FakeData.getPaper())
        }
    }

    @Test
    fun insert() {
        val liveData = database.noteDao().notesLive
        liveData.observeForever(notesObserver)

        Mockito.verify(notesObserver).onChanged(Mockito.any())
        Mockito.verify(notesObserver).onChanged(emptyList())

        val localEventNote = FakeData.getNote(FakeData.getEvent().objectId)
        val localPaperNote = FakeData.getNote(FakeData.getEvent().objectId, FakeData.getPaper().objectId)
        runBlocking {
            database.noteDao().insert(localEventNote)
            database.noteDao().insert(localPaperNote)
        }

        Mockito.verify(notesObserver, Mockito.times(3)).onChanged(Mockito.any())

        val remoteEventNote = localEventNote.apply { objectId = "remoteEventNote" }
        val remotePaperNote = localPaperNote.apply { objectId = "remotePaperNote" }
        runBlocking {
            database.noteDao().insert(remoteEventNote)
            database.noteDao().insert(remotePaperNote)
        }

        Mockito.verify(notesObserver, Mockito.times(5)).onChanged(Mockito.any())
        runBlocking {
            val allNotes = database.noteDao().notes()
            Assert.assertEquals(4, allNotes.size)
        }
    }
}
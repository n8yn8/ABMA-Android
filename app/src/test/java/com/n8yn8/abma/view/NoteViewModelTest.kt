package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.BNote
import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.test.util.FakeData
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class NoteViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var remote: DbManager
    @Mock
    lateinit var noteModelObserver: Observer<List<NoteModel>>

    private val database: AppDatabase by inject()
    private lateinit var noteViewModel: NoteViewModel

    private val event1 = FakeData.getEvent()
    private val paper1 = FakeData.getPaper()
    private val event2 = FakeData.getEvent(index = 2)
    private val paper2 = FakeData.getPaper(index = 2, eventIndex = 2)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        StandAloneContext.startKoin(
                listOf(
                        module {
                            single {
                                Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
                                        .allowMainThreadQueries()
                                        .build()
                            }
                            single { remote }
                        }
                )
        )

        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1, paper2))
        }
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    private fun setUpNoteModel() {
        noteViewModel = NoteViewModel(application)
        noteViewModel.notesData.observeForever(noteModelObserver)
        Mockito.verify(noteModelObserver).onChanged(Mockito.anyList())
    }

    private fun setUpRemoteResponse(bNotes: List<BNote>) {

        Mockito.doAnswer {
            val callback = it.arguments[0] as DbManager.OnGetNotesCallback
            callback.notesRetrieved(bNotes, null)
            null
        }.`when`(remote).getAllNotes(Mockito.any())

        Mockito.doAnswer {
            val bNote = it.arguments[0] as BNote
            bNote.apply {
                created = Date()
                updated = Date()
                objectId = "$eventId$paperId"
            }
            val callback = it.arguments[1] as DbManager.OnNoteSavedCallback
            callback.noteSaved(bNote, null)
            null
        }.`when`(remote).addNote(Mockito.any(), Mockito.any())
    }

    @Test
    fun getRemoteNotes_nonLocalAllRemote() {
        setUpNoteModel()

        val bNotes = listOf(
                FakeData.getBNote(index = "1", inEventId = event1.objectId),
                FakeData.getBNote(index = "2", inEventId = event1.objectId, inPaperId = paper1.objectId)
        )
        setUpRemoteResponse(bNotes)
        noteViewModel.getRemoteNotes()

        Mockito.verify(noteModelObserver, Mockito.times(2)).onChanged(Mockito.anyList())
        Mockito.verify(remote, Mockito.never()).addNote(Mockito.any(), Mockito.any())
//        Mockito.verify(noteModelObserver).onChanged(listOf(
//                NoteModel(FakeData.getNote(event1.objectId), event1, null),
//                NoteModel(FakeData.getNote(event1.objectId, paper1.objectId), event1, paper1)
//        ))
    }

    @Test
    fun getRemoteNotes_allLocalNoneRemote() {
        runBlocking {
            database.noteDao().insert(listOf(FakeData.getNote(event1.objectId), FakeData.getNote(event1.objectId, paper1.objectId)))
        }

        setUpNoteModel()
        setUpRemoteResponse(emptyList())
        noteViewModel.getRemoteNotes()

        //TODO: 3 from no new notes added, but saving after sync. Should be 1
        Mockito.verify(noteModelObserver, Mockito.times(3)).onChanged(Mockito.anyList())
        //Local added to remote
        Mockito.verify(remote, Mockito.times(2)).addNote(Mockito.any(), Mockito.any())
    }

    @Test
    fun getRemoteNotes_bothLocalAndRemote() {
        runBlocking {
            database.noteDao().insert(listOf(FakeData.getNote(event1.objectId), FakeData.getNote(event1.objectId, paper1.objectId)))
        }
        setUpNoteModel()
        val bNotes = listOf(
                FakeData.getBNote(index = "1", inEventId = event2.objectId),
                FakeData.getBNote(index = "2", inEventId = event2.objectId, inPaperId = paper2.objectId)
        )
        setUpRemoteResponse(bNotes)
        noteViewModel.getRemoteNotes()

        //TODO reduce to 2 executions. 4 happens from sync of local notes.
        Mockito.verify(noteModelObserver, Mockito.times(4)).onChanged(Mockito.anyList())
        Mockito.verify(remote, Mockito.times(2)).addNote(Mockito.any(), Mockito.any())
    }
}
package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EventViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var eventPaperObserver: Observer<EventPaperModel>

    @Mock
    lateinit var noteObserver: Observer<Note>

    @Mock
    lateinit var directionObserver: Observer<EventViewModel.DirectionLimit?>

    private val database: AppDatabase by inject()

    private lateinit var eventViewModel: EventViewModel

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
                        }
                )
        )
        eventViewModel = EventViewModel(application)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun setSelectedEvent() {
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(FakeData.getEvent(), FakeData.getEvent(2)))
        }
        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(FakeData.getEvent().objectId)

        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(FakeData.getEvent()))
        Mockito.verify(eventPaperObserver).onChanged(Mockito.any())
    }

    @Test
    fun getPreviousEventSelected() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1, 1)
        val paper2Event1 = FakeData.getPaper(2, 1)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
        }
        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event2.objectId)
        eventViewModel.getPapers(event2.objectId)

        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2))
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event1))
        Mockito.verify(eventPaperObserver).onChanged(Mockito.any())

        eventViewModel.getPrevious()
        Mockito.verify(directionObserver, Mockito.never()).onChanged(Mockito.any())
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))

        eventViewModel.getPrevious()
        Mockito.verify(directionObserver).onChanged(EventViewModel.DirectionLimit.EVENT_MIN)
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1)) //Wasn't called again
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event1, paper1Event1))
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event1, paper2Event1))
        Mockito.verify(eventPaperObserver, Mockito.times(2)).onChanged(Mockito.any())
    }

    @Test
    fun getPreviousPaperSelected() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event2 = FakeData.getPaper(1, 2)
        val paper2Event2 = FakeData.getPaper(2, 2)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event2, paper2Event2))
        }
        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event2.objectId)
        eventViewModel.getPapers(event2.objectId)
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2))
        //Verify no papers yet selected
        Mockito.verify(eventPaperObserver).onChanged(Mockito.any())

        eventViewModel.selectPaper(paper2Event2)
        //Verify only first selected paper selected
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event2, paper1Event2))
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2, paper2Event2))
        Mockito.verify(eventPaperObserver, Mockito.times(2)).onChanged(Mockito.any())

        eventViewModel.getPrevious()
        Mockito.verify(directionObserver, Mockito.never()).onChanged(Mockito.any())

        eventViewModel.getPrevious()
        Mockito.verify(directionObserver).onChanged(EventViewModel.DirectionLimit.PAPER_MIN)

        //Verify original selection of event only
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2))
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event1))
        //Verify original selected paper and previous paper have been selected once
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2, paper1Event2))
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2, paper2Event2))
        Mockito.verify(eventPaperObserver, Mockito.times(3)).onChanged(Mockito.any())
    }

    @Test
    fun getNextEventSelected() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1, 1)
        val paper2Event1 = FakeData.getPaper(2, 1)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
        }
        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)

        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event2))
        Mockito.verify(eventPaperObserver).onChanged(Mockito.any())

        eventViewModel.getNext()
        Mockito.verify(directionObserver, Mockito.never()).onChanged(Mockito.any())
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2))

        eventViewModel.getNext()
        Mockito.verify(directionObserver).onChanged(EventViewModel.DirectionLimit.EVENT_MAX)
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event2)) //Wasn't called again
        Mockito.verify(eventPaperObserver, Mockito.times(2)).onChanged(Mockito.any())
    }

    @Test
    fun getNextPaperSelected() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1)
        val paper2Event1 = FakeData.getPaper(2)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
        }
        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))
        //Verify no papers yet selected
        Mockito.verify(eventPaperObserver).onChanged(Mockito.any())

        eventViewModel.selectPaper(paper1Event1)
        //Verify only first selected paper selected
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1, paper1Event1))
        Mockito.verify(eventPaperObserver, Mockito.never()).onChanged(EventPaperModel(event1, paper2Event1))
        Mockito.verify(eventPaperObserver, Mockito.times(2)).onChanged(Mockito.any())

        eventViewModel.getNext()
        Mockito.verify(directionObserver, Mockito.never()).onChanged(Mockito.any())

        eventViewModel.getNext()
        Mockito.verify(directionObserver).onChanged(EventViewModel.DirectionLimit.PAPER_MAX)

        //Verify original selection of event only
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1))
        //Verify original selected paper and previous paper have been selected once
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1, paper1Event1))
        Mockito.verify(eventPaperObserver).onChanged(EventPaperModel(event1, paper2Event1))
        Mockito.verify(eventPaperObserver, Mockito.times(3)).onChanged(Mockito.any())
    }

    @Test
    fun saveNoteSelectedEvent() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1)
        val paper2Event1 = FakeData.getPaper(2)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
        }

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)

        val expectedNotString = "Event 1 note"
        eventViewModel.saveNote(expectedNotString)

        val notes = mutableListOf<Note>()
        runBlocking { notes.addAll(database.noteDao().notes()) }
        Assert.assertEquals(1, notes.size)
        val resultNote = notes.first()
        Assert.assertNotNull(resultNote)
        Assert.assertEquals(expectedNotString, resultNote.content)
        Assert.assertEquals(event1.objectId, resultNote.eventId)
        Assert.assertNull(resultNote.paperId)
    }

    @Test
    fun saveNoteSelectedPaper() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1)
        val paper2Event1 = FakeData.getPaper(2)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
        }

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)
        eventViewModel.selectPaper(paper1Event1)

        val expectedNotString = "Paper 1 note"
        eventViewModel.saveNote(expectedNotString)

        val notes = mutableListOf<Note>()
        runBlocking { notes.addAll(database.noteDao().notes()) }
        Assert.assertEquals(1, notes.size)
        val resultNote = notes.first()
        Assert.assertNotNull(resultNote)
        Assert.assertEquals(expectedNotString, resultNote.content)
        Assert.assertEquals(event1.objectId, resultNote.eventId)
        Assert.assertEquals(paper1Event1.objectId, resultNote.paperId)
    }

    @Test
    fun deleteNote() {
        val event1 = FakeData.getEvent(1)
        val event2 = FakeData.getEvent(2)
        val paper1Event1 = FakeData.getPaper(1)
        val paper2Event1 = FakeData.getPaper(2)
        val note1 = FakeData.getNote(event1.objectId)
        val note2 = FakeData.getNote(event1.objectId, paper1Event1.objectId)
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(listOf(paper1Event1, paper2Event1))
            database.noteDao().insert(note1)
            database.noteDao().insert(note2)
        }

        //Set DB id's
        note1.apply { id = 1 }
        note2.apply { id = 2 }

        eventViewModel.noteLiveData.observeForever(noteObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)

        Mockito.verify(noteObserver).onChanged(note1)

        eventViewModel.saveNote("")

        Mockito.verify(noteObserver).onChanged(null)

        eventViewModel.selectPaper(paper1Event1)
        Mockito.verify(noteObserver).onChanged(note2)

        eventViewModel.saveNote("")
        Mockito.verify(noteObserver, Mockito.times(2)).onChanged(null)
    }

    @Test
    fun getPapers() {
        val event1 = FakeData.getEvent()
        val event2 = FakeData.getEvent(2)
        val event1Papers = listOf(FakeData.getPaper(1), FakeData.getPaper(2))
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(listOf(event1, event2))
            database.paperDao().insert(event1Papers)
        }

        val retrieved1 = eventViewModel.getPapers(event1.objectId)
        Assert.assertEquals(event1Papers, retrieved1)

        val retrieved2 = eventViewModel.getPapers(event2.objectId)
        Assert.assertEquals(emptyList<Paper>(), retrieved2)
    }
}
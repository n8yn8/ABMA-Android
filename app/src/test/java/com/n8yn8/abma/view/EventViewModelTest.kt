package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.EventPapers
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.test.util.FakeData
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EventViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val application: Application = ApplicationProvider.getApplicationContext()

    private val eventPaperObserver = spyk<Observer<EventPaperModel>>()
    private val noteObserver = spyk<Observer<Note>>()
    private val directionObserver = spyk<Observer<EventViewModel.DirectionLimit>>()

    private val database: AppDatabase by inject()

    private lateinit var eventViewModel: EventViewModel

    @Before
    fun setUp() {
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

        verify {
            eventPaperObserver.onChanged(EventPaperModel(FakeData.getEventPapers()))
            eventPaperObserver.onChanged(any())
        }
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
        val eventPapers1 = EventPapers(event1, listOf(paper1Event1, paper2Event1))
        val eventPapers2 = EventPapers(event2, listOf())

        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event2.objectId)

        verify {
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2))
            eventPaperObserver.onChanged(any())
        }
        verify(exactly = 0) { eventPaperObserver.onChanged(EventPaperModel(eventPapers1)) }

        eventViewModel.getPrevious()
        verify(exactly = 0) { directionObserver.onChanged(any()) }
        verify { eventPaperObserver.onChanged(EventPaperModel(eventPapers1)) }

        eventViewModel.getPrevious()
        verify {
            directionObserver.onChanged(EventViewModel.DirectionLimit.EVENT_MIN)
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1)) //Wasn't called again
        }
        verify(exactly = 0) {
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper1Event1))
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper2Event1))
        }
        verify(exactly = 2) { eventPaperObserver.onChanged(any()) }
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
        val eventPapers1 = EventPapers(event1, listOf())
        val eventPapers2 = EventPapers(event2, listOf(paper1Event2, paper2Event2))

        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event2.objectId)
        verify {
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2))
            //Verify no papers yet selected
            eventPaperObserver.onChanged(any())
        }

        eventViewModel.selectPaper(paper2Event2)
        //Verify only first selected paper selected
        verify(exactly = 0) { eventPaperObserver.onChanged(EventPaperModel(eventPapers2, paper1Event2)) }
        verify { eventPaperObserver.onChanged(EventPaperModel(eventPapers2, paper2Event2)) }
        verify(exactly = 2) { eventPaperObserver.onChanged(any()) }

        eventViewModel.getPrevious()
        verify(exactly = 0) { directionObserver.onChanged(any()) }

        eventViewModel.getPrevious()

        verify(exactly = 0) { eventPaperObserver.onChanged(EventPaperModel(eventPapers1)) }
        verify {
            directionObserver.onChanged(EventViewModel.DirectionLimit.PAPER_MIN)
            //Verify original selection of event only
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2))
            //Verify original selected paper and previous paper have been selected once
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2, paper1Event2))
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2, paper2Event2))
        }
        verify(exactly = 3) { eventPaperObserver.onChanged(any()) }
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
        val eventPapers1 = EventPapers(event1, listOf(paper1Event1, paper2Event1))
        val eventPapers2 = EventPapers(event2, listOf())

        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event1.objectId)

        verify {
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1))
            eventPaperObserver.onChanged(any())
        }
        verify(exactly = 0) { eventPaperObserver.onChanged(EventPaperModel(eventPapers2)) }

        eventViewModel.getNext()
        verify(exactly = 0) { directionObserver.onChanged(any()) }
        verify {
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1))
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2))
        }

        eventViewModel.getNext()
        verify {
            directionObserver.onChanged(EventViewModel.DirectionLimit.EVENT_MAX)
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1))
            eventPaperObserver.onChanged(EventPaperModel(eventPapers2)) //Wasn't called again
        }
        verify(exactly = 2) { eventPaperObserver.onChanged(any()) }
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
        val eventPapers1 = EventPapers(event1, listOf(paper1Event1, paper2Event1))

        eventViewModel.eventPaper.observeForever(eventPaperObserver)
        eventViewModel.directionLimit.observeForever(directionObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        verify{
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1))
            //Verify no papers yet selected
            eventPaperObserver.onChanged(any())
        }

        eventViewModel.selectPaper(paper1Event1)
        //Verify only first selected paper selected
        verify { eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper1Event1)) }
        verify(exactly = 0) { eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper2Event1)) }
        verify(exactly = 2) { eventPaperObserver.onChanged(any()) }

        eventViewModel.getNext()
        verify(exactly = 0) { directionObserver.onChanged(any()) }

        eventViewModel.getNext()

        verify {
            directionObserver.onChanged(EventViewModel.DirectionLimit.PAPER_MAX)
            //Verify original selection of event only
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1))
            //Verify original selected paper and previous paper have been selected once
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper1Event1))
            eventPaperObserver.onChanged(EventPaperModel(eventPapers1, paper2Event1))
        }
        verify(exactly = 3) { eventPaperObserver.onChanged(any()) }
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

        verify { noteObserver.onChanged(note1) }

        eventViewModel.saveNote("")

        verify { noteObserver.onChanged(null) }

        eventViewModel.selectPaper(paper1Event1)
        verify { noteObserver.onChanged(note2) }

        eventViewModel.saveNote("")
        verify(exactly = 2) { noteObserver.onChanged(null) }
    }

}
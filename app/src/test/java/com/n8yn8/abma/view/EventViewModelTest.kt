package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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

private const val DATABASE = "DATABASE"

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EventViewModelTest : KoinTest {

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var eventObserver: Observer<Event>

    @Mock
    lateinit var paperObserver: Observer<Paper?>

    private val database: com.n8yn8.abma.model.AppDatabase by inject()

    private lateinit var eventViewModel: EventViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        StandAloneContext.startKoin(
                listOf(
                        module {
                            single {
                                Room.inMemoryDatabaseBuilder(application, com.n8yn8.abma.model.AppDatabase::class.java)
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
        eventViewModel.event.observeForever(eventObserver)
        eventViewModel.paper.observeForever(paperObserver)

        eventViewModel.setSelectedEvent(FakeData.getEvent().objectId)

        Mockito.verify(eventObserver).onChanged(FakeData.getEvent())
        Mockito.verify(paperObserver, Mockito.never()).onChanged(Mockito.any())
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
        eventViewModel.event.observeForever(eventObserver)
        eventViewModel.paper.observeForever(paperObserver)

        eventViewModel.setSelectedEvent(event2.objectId)
        eventViewModel.getPapers(event2.objectId)

        Mockito.verify(eventObserver).onChanged(event2)
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event1)

        val canGetPrevious = eventViewModel.getPrevious()
        assert(canGetPrevious)
        Mockito.verify(eventObserver).onChanged(event1)

        val canGetPreviousAgain = eventViewModel.getPrevious()
        assert(!canGetPreviousAgain)
        Mockito.verify(eventObserver).onChanged(event1) //Wasn't called again
        Mockito.verify(paperObserver, Mockito.never()).onChanged(Mockito.any())
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
        eventViewModel.event.observeForever(eventObserver)
        eventViewModel.paper.observeForever(paperObserver)

        eventViewModel.setSelectedEvent(event2.objectId)
        eventViewModel.getPapers(event2.objectId)
        Mockito.verify(eventObserver).onChanged(event2)
        //Verify no papers yet selected
        Mockito.verify(paperObserver, Mockito.never()).onChanged(Mockito.any())

        eventViewModel.paper.postValue(paper2Event2)
        //Verify only first selected paper selected
        Mockito.verify(paperObserver, Mockito.never()).onChanged(paper1Event2)
        Mockito.verify(paperObserver, Mockito.times(1)).onChanged(paper2Event2)

        val canGetPrevious = eventViewModel.getPrevious()
        assert(canGetPrevious)
        //Verify original selection of event only
        Mockito.verify(eventObserver, Mockito.times(1)).onChanged(event2)
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event1)
        //Verify original selected paper and previous paper have been selected once
        Mockito.verify(paperObserver, Mockito.times(1)).onChanged(paper1Event2)
        Mockito.verify(paperObserver, Mockito.times(1)).onChanged(paper2Event2)

        val canGetPreviousAgain = eventViewModel.getPrevious()
        assert(!canGetPreviousAgain)
        Mockito.verify(eventObserver, Mockito.times(1)).onChanged(event2) //Wasn't called again
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event1) // Never navigated to event1
        //Verify original selected paper and previous paper have been selected once still
        Mockito.verify(paperObserver, Mockito.times(1)).onChanged(paper1Event2)
        Mockito.verify(paperObserver, Mockito.times(1)).onChanged(paper2Event2)
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
        eventViewModel.event.observeForever(eventObserver)
        eventViewModel.paper.observeForever(paperObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)

        Mockito.verify(eventObserver).onChanged(event1)
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event2)

        val canGetNext = eventViewModel.getNext()
        assert(canGetNext)
        Mockito.verify(eventObserver).onChanged(event1)
        Mockito.verify(eventObserver).onChanged(event2)

        val canGetNextAgain = eventViewModel.getNext()
        assert(!canGetNextAgain)
        Mockito.verify(eventObserver).onChanged(event1)
        Mockito.verify(eventObserver).onChanged(event2) //Wasn't called again
        Mockito.verify(paperObserver, Mockito.never()).onChanged(Mockito.any())
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
        eventViewModel.event.observeForever(eventObserver)
        eventViewModel.paper.observeForever(paperObserver)

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)
        Mockito.verify(eventObserver).onChanged(event1)
        //Verify no papers yet selected
        Mockito.verify(paperObserver, Mockito.never()).onChanged(Mockito.any())

        eventViewModel.paper.postValue(paper1Event1)
        //Verify only first selected paper selected
        Mockito.verify(paperObserver).onChanged(paper1Event1)
        Mockito.verify(paperObserver, Mockito.never()).onChanged(paper2Event1)

        val canGetPrevious = eventViewModel.getNext()
        assert(canGetPrevious)
        //Verify original selection of event only
        Mockito.verify(eventObserver).onChanged(event1)
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event2)
        //Verify original selected paper and previous paper have been selected once
        Mockito.verify(paperObserver).onChanged(paper1Event1)
        Mockito.verify(paperObserver).onChanged(paper2Event1)

        val canGetPreviousAgain = eventViewModel.getNext()
        assert(!canGetPreviousAgain)
        Mockito.verify(eventObserver).onChanged(event1) //Wasn't called again
        Mockito.verify(eventObserver, Mockito.never()).onChanged(event2) // Never navigated to event1
        //Verify original selected paper and previous paper have been selected once still
        Mockito.verify(paperObserver).onChanged(paper1Event1)
        Mockito.verify(paperObserver).onChanged(paper2Event1)
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

        val notes = database.noteDao().notes
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
        eventViewModel.paper.postValue(paper1Event1)

        val expectedNotString = "Paper 1 note"
        eventViewModel.saveNote(expectedNotString)

        val notes = database.noteDao().notes
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

        eventViewModel.setSelectedEvent(event1.objectId)
        eventViewModel.getPapers(event1.objectId)

        val eventNoteBeforeRemove = eventViewModel.getNote(event1.objectId)
        Assert.assertNotNull(eventNoteBeforeRemove)

        eventViewModel.saveNote("")

        val eventNote = eventViewModel.getNote(event1.objectId)
        Assert.assertNull(eventNote)
        val paperNoteBeforeRemove = eventViewModel.getNote(event1.objectId, paper1Event1.objectId)
        Assert.assertNotNull(paperNoteBeforeRemove)

        eventViewModel.paper.postValue(paper1Event1)
        eventViewModel.saveNote("")
        val paperNoteAfterRemove = eventViewModel.getNote(event1.objectId, paper1Event1.objectId)
        Assert.assertNull(paperNoteAfterRemove)
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
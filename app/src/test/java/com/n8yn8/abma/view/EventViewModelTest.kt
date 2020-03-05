package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.Observer
import androidx.room.Room
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.After
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
class EventViewModelTest: KoinTest {

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var eventObserver: Observer<Event>

    private val database: com.n8yn8.abma.model.AppDatabase by inject()

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
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun getEvent() {

    }

    @Test
    fun getPaper() {
    }

    @Test
    fun getEventPapers() {
    }

    @Test
    fun setEventPapers() {
    }

    @Test
    fun setSelectedEvent() {
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
        }
        val eventViewModel = EventViewModel(application)
        eventViewModel.event.observeForever(eventObserver)

        eventViewModel.setSelectedEvent(FakeData.getEvent().objectId)

        Mockito.verify(eventObserver).onChanged(FakeData.getEvent())
    }

    @Test
    fun getPrevious() {
    }

    @Test
    fun getNext() {
    }

    @Test
    fun saveNote() {
    }

    @Test
    fun getPapers() {
    }

    @Test
    fun getNote() {
    }
}
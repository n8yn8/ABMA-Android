package com.n8yn8.abma.view

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.test.util.FakeData
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ScheduleViewModelTest : KoinTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val application: Application = ApplicationProvider.getApplicationContext()
    private val eventsObserver = spyk<Observer<List<Event>>>()

    private val database: AppDatabase by inject()

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

        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }
    }

    @After
    fun onDone() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun initializeEmpty() {
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)

        verify(exactly = 0) { eventsObserver.onChanged(emptyList()) }
    }

    @Test
    fun setYear_noEvents() {
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        verify(exactly = 0) {
            eventsObserver.onChanged(emptyList())
            eventsObserver.onChanged(any())
        }
    }

    @Test
    fun setYear_noEvents_receiveEvents() {
        setYear_noEvents()

        runBlocking { database.eventDao().insert(FakeData.getEvent()) }

        verify {
            eventsObserver.onChanged(listOf(FakeData.getEvent()))
            eventsObserver.onChanged(any())
        }
    }

    @Test
    fun setYear_withEvents() {
        val events = listOf(FakeData.getEvent(1), FakeData.getEvent(2))
        runBlocking {
            database.eventDao().insert(events)
        }
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        verify {
            eventsObserver.onChanged(events)
            eventsObserver.onChanged(any())
        }
    }

    @Test
    fun receiveUpdateCurrentDay_differentYear() {
        val events = mutableListOf(FakeData.getEvent(1), FakeData.getEvent(2))
        runBlocking {
            database.eventDao().insert(events)
        }
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        val differentYearEvent = FakeData.getEvent(index = 3, yearName = 2019)
        runBlocking {
            database.yearDao().insert(FakeData.getYear(name = 2019))
            database.eventDao().insert(differentYearEvent)
        }

        verify { eventsObserver.onChanged(events) }
        events.add(differentYearEvent)
        verify(exactly = 0) { eventsObserver.onChanged(events) }
        verify { eventsObserver.onChanged(any()) }
    }

    @Test
    fun receiveUpdateCurrentDay_sameYear() {
        val events = mutableListOf(FakeData.getEvent(1), FakeData.getEvent(2))
        runBlocking {
            database.eventDao().insert(events)
        }
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        val sameYearEvent = FakeData.getEvent(3)
        runBlocking {
            database.eventDao().insert(sameYearEvent)
        }

        verify { eventsObserver.onChanged(events) }
        events.add(sameYearEvent)
        verify { eventsObserver.onChanged(events) }
        verify(exactly = 2) { eventsObserver.onChanged(any()) }

        val differentDayEvent = FakeData.getEvent(25)
        runBlocking {
            database.eventDao().insert(differentDayEvent)
        }

        verify(exactly = 2) { eventsObserver.onChanged(any()) }
    }

    @Test
    fun nextPrevious_available() {
        val eventOne = FakeData.getEvent(1)
        val eventTwo = FakeData.getEvent(25)
        runBlocking {
            database.eventDao().insert(eventOne)
            database.eventDao().insert(eventTwo)

        }
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        verify { eventsObserver.onChanged(listOf(eventOne)) }

        scheduleViewModel.nextDay()
        verify { eventsObserver.onChanged(listOf(eventTwo)) }

        scheduleViewModel.previousDay()
        verify(exactly = 2) { eventsObserver.onChanged(listOf(eventOne)) }
    }

    @Test
    fun prevNext_notAvailable() {
        val events = listOf(FakeData.getEvent(1), FakeData.getEvent(2))
        runBlocking {
            database.eventDao().insert(events)

        }
        val scheduleViewModel = ScheduleViewModel(application)
        scheduleViewModel.scheduleViewData.observeForever(eventsObserver)
        scheduleViewModel.setSelectedYear(FakeData.getYear())

        verify { eventsObserver.onChanged(any()) }

        scheduleViewModel.nextDay()
        scheduleViewModel.previousDay()

        verify { eventsObserver.onChanged(any()) }
    }

}
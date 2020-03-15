package com.n8yn8.abma.model.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    @Mock
    lateinit var eventObserver: Observer<List<Event>>
    @Mock
    lateinit var eventUniqueObserver: Observer<List<Event>>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
        }
    }

    @Test
    fun testUpdateEvents() {
        val from = 1L
        val to = 10L
        val eventLiveData = database.eventDao().getAllEventsFor(from, to)
        eventLiveData.observeForever(eventObserver)

        val eventUniqueLiveData = Transformations.distinctUntilChanged(eventLiveData)
        eventUniqueLiveData.observeForever(eventUniqueObserver)

        Mockito.verify(eventObserver).onChanged(emptyList())
        Mockito.verify(eventUniqueObserver).onChanged(emptyList())

        val eventInRange = FakeData.getEvent().apply {
            startDate = 1
            endDate = 2
        }

        database.eventDao().insert(eventInRange)
        Mockito.verify(eventObserver).onChanged(listOf(eventInRange))
        Mockito.verify(eventUniqueObserver).onChanged(listOf(eventInRange))

        val event2InRange = FakeData.getEvent(2).apply {
            startDate = 9
            endDate = 10
        }

        database.eventDao().insert(event2InRange)
        Mockito.verify(eventObserver).onChanged(listOf(eventInRange, event2InRange))
        Mockito.verify(eventUniqueObserver).onChanged(listOf(eventInRange, event2InRange))

        val eventOutOfRange = FakeData.getEvent(3).apply {
            startDate = 11
            endDate = 12
        }

        database.eventDao().insert(eventOutOfRange)
        //Event outside not included in data set returned
        Mockito.verify(eventObserver, Mockito.never()).onChanged(listOf(eventInRange, event2InRange, eventOutOfRange))
        //There is a second call on the correct set
        Mockito.verify(eventObserver, Mockito.times(2)).onChanged(listOf(eventInRange, event2InRange))
        //There was no change in the unique set
        Mockito.verify(eventUniqueObserver).onChanged(listOf(eventInRange, event2InRange))
    }

}
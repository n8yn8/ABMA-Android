package com.n8yn8.abma.model

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Year
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    var database: AppDatabase? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, AppDatabase::class.java).build()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        database!!.close()
    }

    @Test
    fun testInsert() {
        val year = year
        val event = getEvent(year.objectId)
        runBlocking {
            database!!.yearDao().insert(year)
            database!!.eventDao().insert(event)
        }

        val years = mutableListOf<Year>()
        runBlocking {
            years.addAll(database!!.yearDao().years())
        }
        Assert.assertEquals(years.size.toLong(), 1)

        val events = mutableListOf<Event>()
        runBlocking {
            events.addAll(database!!.eventDao().getEvents(years[0].objectId))
        }

        Assert.assertEquals(events.size.toLong(), 1)
        Assert.assertEquals(year, years[0])
        Assert.assertEquals(event, events[0])
    }

    private val year: Year
        get() {
            val year = Year()
            year.id = 1
            year.info = "info"
            year.name = 2016
            year.objectId = "id1"
            year.welcome = "welcome"
            return year
        }

    companion object {
        fun getEvent(yearObjectId: String?): Event {
            val event = Event()
            event.details = "details"
            event.endDate = 5678L
            event.startDate = 1234L
            event.id = 1
            event.objectId = "id1"
            event.place = "place"
            event.title = "title"
            event.subtitle = "subtitle"
            event.yearId = yearObjectId
            return event
        }
    }
}
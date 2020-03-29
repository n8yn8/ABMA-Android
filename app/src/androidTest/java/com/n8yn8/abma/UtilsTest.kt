package com.n8yn8.abma

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.AppDatabaseTest
import com.n8yn8.abma.model.backendless.BEvent
import com.n8yn8.abma.model.entities.Year
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class UtilsTest {
    @Test
    fun startOfDay() {
        val dateStart = Utils.getStartOfDay(1554508800000L)
        Assert.assertEquals(1554508800000L, dateStart)
        val dateStart2 = Utils.getStartOfDay(1554508800001L)
        Assert.assertEquals(1554508800000L, dateStart2)
        val dateMillis = Utils.getStartOfDay(1554552000000L)
        Assert.assertEquals(1554508800000L, dateMillis)
    }

    @Test
    fun testDeleteEvents() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = AppDatabase.getInstance(context.applicationContext)
        val yearId = "yearId"
        val year = Year()
        year.objectId = yearId
        runBlocking { db.yearDao().insert(year) }
        val remoteEvents: MutableList<BEvent> = ArrayList()
        for (i in 1..9) {
            val event = AppDatabaseTest.getEvent(yearId)
            event.objectId = i.toString()
            db.eventDao().insert(event)
            if (i % 2 == 0) {
                val bEvent = BEvent()
                bEvent.objectId = i.toString()
                bEvent.details = event.details
                bEvent.endDate = Date(event.endDate)
                bEvent.location = event.place
                bEvent.startDate = Date(event.startDate)
                remoteEvents.add(bEvent)
            }
        }
        Utils.saveEvents(db, yearId, remoteEvents)
        val savedEvents = db.eventDao().getEvents(yearId)
        Assert.assertEquals(4, savedEvents.size.toLong())
        for (savedEvent in savedEvents) {
            val objectId = savedEvent.objectId.toInt()
            Assert.assertEquals(0, objectId % 2.toLong())
        }
    }
}
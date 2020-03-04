package com.n8yn8.abma.model.dao

import com.n8yn8.abma.BaseTest
import com.n8yn8.abma.FakeData
import junit.framework.Assert
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EventDaoTest : BaseTest() {
    override fun setUp() {
        super.setUp()
        initDatabase()
    }

    @Test
    fun getEventData() {
        runBlocking {
            val events = database.eventDao().events
            Assert.assertEquals(1, events.size)
        }
    }

    private fun initDatabase() {
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
        }
    }
}
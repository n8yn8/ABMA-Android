package com.n8yn8.test.util

import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.abma.model.entities.Year
import java.util.concurrent.TimeUnit

object FakeData {

    fun getYear(name: Int = 2020, id: Int = name) : Year {
        return Year().apply {
            objectId = "year$name"
            this.name = name
            this.id = id
        }
    }

    fun getBYear(name: Int = 2020) : BYear {
        return BYear().apply {
            objectId = "year$name"
            this.name = name
        }
    }

    fun getEvent(index: Int = 1) : Event {
        return Event().apply {
            id = index
            objectId = "event$index"
            yearId = "year2020"
            details = "Some details"
            endDate = 1582256420000 + TimeUnit.HOURS.toMillis(index.toLong()) // default 02-20-2020 20:40:20
            startDate = 1582255220000 + TimeUnit.HOURS.toMillis(index.toLong()) // default 02-20-2020 20:20:20
            place = "Here"
            title = "Some Title"
            subtitle = "Some subtitle"
        }
    }

    fun getPaper(index: Int, eventIndex: Int = 1) : Paper {
        return Paper().apply {
            id = index
            objectId = "paper$index"
            eventId = "event$eventIndex"
            title = "$index Paper"
            author = "$index Author"
            synopsis = "This is a synopsis"
            order = index
        }
    }

    fun getNote(inEventId: String, inPaperId: String? = null) : Note {
        return Note().apply {
            eventId = inEventId
            paperId = inPaperId
            content = "Content of a note for $inEventId & $inPaperId"
        }
    }
}
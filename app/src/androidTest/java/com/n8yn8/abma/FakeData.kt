package com.n8yn8.abma

import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.abma.model.entities.Year

object FakeData {

    fun getYear() : Year {
        return Year().apply {
            objectId = "2020"
        }
    }

    fun getEvent() : Event {
        return Event().apply {
            id = 1
            objectId = "event1"
            yearId = "2020"
            details = "Some details"
            endDate = 1582260020000 // 02-20-2020 20:40:20
            startDate = 1582258820000 // 02-20-2020 20:20:20
            place = "Here"
            title = "Some Title"
            subtitle = "Some subtitle"
        }
    }

    fun getPaper(index: Int) : Paper {
        return Paper().apply {
            id = index
            objectId = "paper$index"
            eventId = "event1"
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
package com.n8yn8.test.util

import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.abma.model.entities.Year
import java.util.*
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

    fun getEvent(index: Int = 1, yearName: Int = 2020) : Event {
        return Event().apply {
            id = index
            objectId = "event$index"
            yearId = "year$yearName"
            details = "Some details"
            endDate = getStartOfYear(yearName, index) // default 02-20-2020 20:40:20
            startDate = getStartOfYear(yearName, index) // default 02-20-2020 20:20:20
            place = "Here"
            title = "Some Title"
            subtitle = "Some subtitle"
        }
    }

    private fun getStartOfYear(yearName: Int, hour: Int): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar[Calendar.YEAR] = yearName
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.DATE] = 2
        calendar[Calendar.MONTH] = 2
        return calendar.timeInMillis
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
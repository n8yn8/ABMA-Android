package com.n8yn8.test.util

import com.n8yn8.abma.model.backendless.BEvent
import com.n8yn8.abma.model.backendless.BNote
import com.n8yn8.abma.model.backendless.BSponsor
import com.n8yn8.abma.model.backendless.BYear
import com.n8yn8.abma.model.entities.*
import java.util.*

object FakeData {

    fun getYear(name: Int = 2020, id: Int = name): Year {
        return Year().apply {
            objectId = "year$name"
            this.name = name
            this.id = id
        }
    }

    fun getBYear(name: Int = 2020): BYear {
        return BYear().apply {
            objectId = "year$name"
            this.name = name
        }
    }

    fun getEvent(index: Int = 1, yearName: Int = 2020): Event {
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

    fun getBEvent(index: Int = 1): BEvent {
        return BEvent().apply {
            objectId = "event$index"
            details = "Some details"
            endDate = Date(getStartOfYear(index, index)) // default 02-20-2020 20:40:20
            startDate = Date(getStartOfYear(index, index)) // default 02-20-2020 20:20:20
            location = "Here"
            title = "Some Title"
            subtitle = "Some subtitle"
            papersCount = 1
        }
    }

    private fun getStartOfYear(yearName: Int, hour: Int): Long {
        val daysAdded = hour / 24
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar[Calendar.YEAR] = yearName
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        calendar[Calendar.DATE] = 2 + daysAdded
        calendar[Calendar.MONTH] = 2
        return calendar.timeInMillis
    }

    fun getPaper(index: Int = 1, eventIndex: Int = 1): Paper {
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

    fun getNote(inEventId: String, inPaperId: String? = null, index: String? = null): Note {
        return Note().apply {
            eventId = inEventId
            paperId = inPaperId
            content = "Content of a note for $inEventId & $inPaperId"
            objectId = index
        }
    }

    fun getBNote(index: String? = "1", inEventId: String, inPaperId: String? = null): BNote {
        return BNote().apply {
            objectId = index
            eventId = inEventId
            paperId = inPaperId
            content = "Content of a note for $inEventId & $inPaperId"
            created = Date()
            updated = Date()
        }
    }

    fun getBSponsors(): List<BSponsor> {
        val list = arrayListOf<BSponsor>()
        for (i in 1..5) {
            list.add(getBSponsor("$i"))
        }
        return list
    }

    private fun getBSponsor(objectId: String): BSponsor {
        return BSponsor().apply {
            this.objectId = objectId
            url = ""
            imageUrl = ""
        }
    }

    fun getSponsor(): Sponsor {
        return Sponsor().apply {
            id = 1
            yearId = getYear().objectId
            url = ""
            imageUrl = ""
        }
    }
}
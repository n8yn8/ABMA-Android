package com.n8yn8.abma.view

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.n8yn8.abma.Utils
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Year
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.TimeUnit

class ScheduleViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()

    private var displayDateMillis: Long = 0
    private var selectedYear: Year? = null

    private val _scheduleViewData = MutableLiveData<ScheduleViewData>()
    val scheduleViewData: LiveData<ScheduleViewData>
        get() = _scheduleViewData

    fun setSelectedYear(year: Year) {
        selectedYear = year
        val events = db.eventDao().getEvents(year.objectId)
        displayDateMillis = Utils.getStartOfDay(events.first().startDate)
        updateDay()
    }

    fun nextDay() {
        val previousEvent = db.eventDao().getEventBefore(selectedYear!!.objectId, displayDateMillis)
        if (previousEvent != null) {
            displayDateMillis = Utils.getStartOfDay(previousEvent.startDate)
            updateDay()
        } else {
            Toast.makeText(getApplication(), "First event reached", Toast.LENGTH_SHORT).show()
        }
    }

    fun previousDay() {
        val nextEvent = db.eventDao().getEventAfter(selectedYear!!.objectId, displayDateMillis + TimeUnit.DAYS.toMillis(1))
        if (nextEvent != null) {
            displayDateMillis = Utils.getStartOfDay(nextEvent.startDate)
            updateDay()
        } else {
            Toast.makeText(getApplication(), "Last event reached", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDay() {
        val day = db.eventDao().getAllEventsFor(displayDateMillis, displayDateMillis + TimeUnit.HOURS.toMillis(24))
        _scheduleViewData.postValue(ScheduleViewData(displayDateMillis, day))
    }
}

class ScheduleViewData(
        val displayDateMillis: Long,
        val day: List<Event>
)
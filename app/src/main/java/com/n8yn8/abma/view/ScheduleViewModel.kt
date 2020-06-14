package com.n8yn8.abma.view

import android.app.Application
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.n8yn8.abma.Utils
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Year
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ScheduleViewModel @ViewModelInject constructor(
        application: Application,
        private val db: AppDatabase
) : AndroidViewModel(application) {

    private var displayDateMillisLD = MutableLiveData<Long>()
    private var selectedYear = MutableLiveData<Year>()

    //monitor for events to be downloaded
    private val yearEvents: LiveData<List<Event>>
    private val yearEventsObserver: Observer<List<Event>>

    val scheduleViewData: LiveData<List<Event>>

    init {
        scheduleViewData = Transformations.switchMap(displayDateMillisLD) { display ->
            Transformations.distinctUntilChanged(db.eventDao().getAllEventsFor(display, display + TimeUnit.HOURS.toMillis(24)))
        }
        yearEvents = Transformations.switchMap(selectedYear) { year ->
            db.eventDao().getEventsLive(year.objectId)
        }
        yearEventsObserver = Observer { allYearEvents ->
            if (allYearEvents.isNotEmpty()) {
                displayDateMillisLD.postValue(Utils.getStartOfDay(allYearEvents.first().startDate))
                removeObserver()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeObserver()
    }

    private fun removeObserver() {
        yearEvents.removeObserver(yearEventsObserver)
    }

    fun setSelectedYear(year: Year) {
        selectedYear.postValue(year)
        viewModelScope.launch {
            val events = db.eventDao().getEvents(year.objectId)
            if (events.isEmpty()) {
                yearEvents.observeForever(yearEventsObserver)
            } else {
                displayDateMillisLD.postValue(Utils.getStartOfDay(events.first().startDate))
            }
        }
    }

    fun nextDay() {
        val displayDateMillis = displayDateMillisLD.value ?: return
        viewModelScope.launch {
            val nextEvent = db.eventDao().getEventAfter(selectedYear.value!!.objectId, displayDateMillis + TimeUnit.DAYS.toMillis(1))
            if (nextEvent != null) {
                displayDateMillisLD.postValue(Utils.getStartOfDay(nextEvent.startDate))
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Last event reached", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun previousDay() {
        val displayDateMillis = displayDateMillisLD.value ?: return
        viewModelScope.launch {
            val previousEvent = db.eventDao().getEventBefore(selectedYear.value!!.objectId, displayDateMillis)
            if (previousEvent != null) {
                displayDateMillisLD.postValue(Utils.getStartOfDay(previousEvent.startDate))
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "First event reached", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.*
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.EventPapers
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class EventViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    enum class DirectionLimit {
        EVENT_MIN, EVENT_MAX, PAPER_MIN, PAPER_MAX
    }

    private val _eventPaper = MediatorLiveData<EventPaperModel>()
    val eventPaper: LiveData<EventPaperModel>
        get() = _eventPaper

    private val _event: MutableLiveData<EventPapers> = MutableLiveData()
    private val _paper: MutableLiveData<Paper?> = MutableLiveData()

    val noteLiveData: LiveData<Note>

    private val _directionLimit = MutableLiveData<DirectionLimit>()
    val directionLimit: LiveData<DirectionLimit>
        get() = _directionLimit

    private val db: AppDatabase by inject()

    init {
        _eventPaper.addSource(_event) {
            _eventPaper.value = combine()
        }
        _eventPaper.addSource(_paper) {
            _eventPaper.value = combine()
        }
        noteLiveData = Transformations.switchMap(_eventPaper) {
            when (it.paper?.objectId) {
                null -> {
                    db.noteDao().getNoteLive(it.eventPapers.event.objectId)
                }
                else -> {
                    db.noteDao().getNoteLive(it.eventPapers.event.objectId, it.paper.objectId)
                }
            }
        }
    }

    private fun combine() : EventPaperModel {
        val thisEvent = _event.value!!
        val thisPaper = _paper.value

        return EventPaperModel(thisEvent, thisPaper)
    }

    fun setSelectedEvent(eventId: String) {
        viewModelScope.launch {
            val initialEvent = db.eventDao().getEventById(eventId)
            _event.postValue(initialEvent)
        }
    }

    fun selectPaper(paper: Paper?) {
        _paper.postValue(paper)
    }

    fun getPrevious() {
        viewModelScope.launch {
            if (_paper.value != null) {
                val prevPaper: Paper? = getPrevPaper()
                if (prevPaper != null) {
                    _paper.postValue(prevPaper)
                } else {
                    _directionLimit.postValue(DirectionLimit.PAPER_MIN)
                }
            } else {
                val tempEvent = db.eventDao().getEventBefore(_event.value!!.event.startDate)
                if (tempEvent != null) {
                    _event.postValue(tempEvent)
                } else {
                    _directionLimit.postValue(DirectionLimit.EVENT_MIN)
                }
            }
        }
    }

    fun getNext() {
        viewModelScope.launch {
            if (_paper.value != null) {
                val nextPaper: Paper? = getNextPaper()
                if (nextPaper != null) {
                    _paper.postValue(nextPaper)
                } else {
                    _directionLimit.postValue(DirectionLimit.PAPER_MAX)
                }
            } else {
                val tempEvent = db.eventDao().getEventAfter(_event.value!!.event.startDate)
                if (tempEvent != null) {
                    _event.postValue(tempEvent)
                } else {
                    _directionLimit.postValue(DirectionLimit.EVENT_MAX)
                }
            }
        }
    }

    fun saveNote(noteString: String): Boolean {
        var eventId: String? = null
        if (_event.value != null) {
            eventId = _event.value?.event?.objectId
        }
        var paperId: String? = null
        if (_paper.value != null) {
            paperId = _paper.value?.objectId
        }
        val noteContent: String = noteString

        var note = noteLiveData.value
        if (noteContent != "") {
            if (note == null) {
                note = Note()
            }
            note.let {
                it.content = noteContent
                it.eventId = eventId
                it.paperId = paperId
            }
            viewModelScope.launch {
                db.noteDao().insert(note)
            }
            DbManager.getInstance().addNote(ConvertUtil.convert(note)) { savedNote, error ->
                if (error == null) {
                    if (savedNote != null) {
                        viewModelScope.launch {
                            db.noteDao().insert(ConvertUtil.convert(savedNote))
                        }
                    }
                }
            }
        } else {
            if (note != null) {
                viewModelScope.launch { db.noteDao().delete(note) }
                DbManager.getInstance().delete(ConvertUtil.convert(note))
                return false
            }
        }
        return true //TODO check this
    }

    private fun getPrevPaper(): Paper? {
        val eventPapers = _event.value ?: return null
        for ((i, checkPaper) in eventPapers.papers.withIndex()) {
            if (checkPaper.objectId == _paper.value?.objectId) {
                return if (i > 0) {
                    eventPapers.papers[i - 1]
                } else {
                    null
                }
            }
        }
        return null
    }

    private fun getNextPaper(): Paper? {
        val eventPapers = _event.value ?: return null
        for ((i, checkPaper) in eventPapers.papers.withIndex()) {
            if (checkPaper.objectId == _paper.value?.objectId) {
                return if (i < eventPapers.papers.size - 1) {
                    eventPapers.papers[i + 1]
                } else {
                    null
                }
            }
        }
        return null
    }
}

data class EventPaperModel(val eventPapers: EventPapers, val paper: Paper? = null)
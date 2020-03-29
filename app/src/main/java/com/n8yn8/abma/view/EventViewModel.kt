package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class EventViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    enum class DirectionLimit {
        EVENT_MIN, EVENT_MAX, PAPER_MIN, PAPER_MAX
    }

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event>
        get() = _event
    val paper: MutableLiveData<Paper?> = MutableLiveData()
    var eventPapers: List<Paper>? = null
        private set
    private val _directionLimit = MutableLiveData<DirectionLimit>()
    val directionLimit: LiveData<DirectionLimit>
        get() = _directionLimit
    private val db: AppDatabase by inject()

    fun setSelectedEvent(eventId: String) {
        viewModelScope.launch {
            val initialEvent = db.eventDao().getEventById(eventId)
            _event.postValue(initialEvent)
        }
    }

    fun getPrevious() {
        viewModelScope.launch {
            if (paper.value != null) {
                val prevPaper: Paper? = getPrevPaper()
                if (prevPaper != null) {
                    paper.postValue(prevPaper)
                } else {
                    _directionLimit.postValue(DirectionLimit.PAPER_MIN)
                }
            } else {
                val tempEvent: Event? = db.eventDao().getEventBefore(_event.value!!.startDate)
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
            if (paper.value != null) {
                val nextPaper: Paper? = getNextPaper()
                if (nextPaper != null) {
                    paper.postValue(nextPaper)
                } else {
                    _directionLimit.postValue(DirectionLimit.PAPER_MAX)
                }
            } else {
                val tempEvent: Event? = db.eventDao().getEventAfter(_event.value!!.startDate)
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
            eventId = _event.value?.objectId
        }
        var paperId: String? = null
        if (paper.value != null) {
            paperId = paper.value?.objectId
        }
        val noteContent: String = noteString

        var note = getNote(eventId, paperId)
        if (noteContent != "") {
            if (note == null) {
                note = Note()
            }
            note.let {
                it.content = noteContent
                it.eventId = eventId
                it.paperId = paperId
            }
            db.noteDao().insert(note)
            DbManager.getInstance().addNote(ConvertUtil.convert(note)) { savedNote, error ->
                if (error == null) {
                    if (savedNote != null) {
                        db.noteDao().insert(ConvertUtil.convert(savedNote))
                    }
                }
            }
        } else {
            if (note != null) {
                db.noteDao().delete(note)
                DbManager.getInstance().delete(ConvertUtil.convert(note))
                return false
            }
        }
        return true //TODO check this
    }

    fun getPapers(eventId: String): List<Paper> {
        val papers = db.paperDao().getPapers(eventId)
        eventPapers = papers
        return papers
    }

    private fun getPrevPaper(): Paper? {
        for (i in eventPapers!!.indices) {
            val checkPaper = eventPapers!![i]
            if (checkPaper.objectId == paper.value?.objectId) {
                return if (i > 0) {
                    eventPapers!![i - 1]
                } else {
                    null
                }
            }
        }
        return null
    }

    private fun getNextPaper(): Paper? {
        for (i in eventPapers!!.indices) {
            val checkPaper = eventPapers!![i]
            if (checkPaper.objectId == paper.value?.objectId) {
                return if (i < eventPapers!!.size - 1) {
                    eventPapers!![i + 1]
                } else {
                    null
                }
            }
        }
        return null
    }

    fun getNote(eventId: String?, paperId: String? = null): Note? {
        return when {
            eventId == null -> {
                return null
            }
            paperId == null -> {
                db.noteDao().getNote(eventId)
            }
            else -> {
                db.noteDao().getNote(eventId, paperId)
            }
        }
    }
}
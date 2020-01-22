package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper

class EventViewModel(application: Application) : AndroidViewModel(application) {

    val event = MutableLiveData<Event>()
    var note: Note? = null
    val paper: MutableLiveData<Paper?> = MutableLiveData()
    var eventPapers: List<Paper>? = null
    private val db = AppDatabase.getInstance(application)

    fun getPrevious() : Boolean {
        if (paper.value != null) {
            val prevPaper: Paper? = getPrevPaper()
            if (prevPaper != null) {
                paper.postValue(prevPaper)
            } else {
                return false
            }
        } else {
            val tempEvent: Event? = db.eventDao().getEventBefore(event.value!!.startDate)
            if (tempEvent != null) {
                event.postValue(tempEvent)
            } else {
                return false
            }
        }

        return true
    }

    fun getNext() : Boolean {
        if (paper.value != null) {
            val nextPaper: Paper? = getNextPaper()
            if (nextPaper != null) {
                paper.postValue(nextPaper)
            } else {
                return false
            }
        } else {
            val tempEvent: Event? = db.eventDao().getEventAfter(event.value!!.startDate)
            if (tempEvent != null) {
                event.postValue(tempEvent)
            } else {
                return false
            }
        }
        return true
    }

    fun saveNote(noteString: String) : Boolean {
        var eventId: String? = null
        if (event.value != null) {
            eventId = event.value?.objectId
        }
        var paperId: String? = null
        if (paper.value != null) {
            paperId = paper.value?.objectId
        }
        val noteContent: String = noteString

        if (noteContent != "") {
            if (note == null) {
                note = Note()
            }
            note.let {
                it?.content = noteContent
                it?.eventId = eventId
                it?.paperId = paperId
            }
            db.noteDao().insert(note)
            DbManager.getInstance().addNote(ConvertUtil.convert(note)) { note, error ->
                if (error == null) {
                    if (note != null) {
                        db.noteDao().insert(ConvertUtil.convert(note))
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

    private fun getPrevPaper(): Paper? {
        for (i in eventPapers!!.indices) {
            val checkPaper = eventPapers!![i]
            if (checkPaper.objectId == paper.value?.objectId) {
                return if (i > 0) {
                    eventPapers!![i - 1].also { paper.postValue(it) }
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
                    eventPapers!![i + 1].also { paper.postValue(it) }
                } else {
                    null
                }
            }
        }
        return null
    }
}
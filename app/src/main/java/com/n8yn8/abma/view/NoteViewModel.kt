package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.NoteEventPaper
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NoteViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()

    val notesData: LiveData<List<NoteEventPaper>>

    init {
        notesData = db.noteDao().notesLive
    }

    fun deleteNote(noteModel: NoteEventPaper?) {
        if (noteModel == null) {
            return
        }
        viewModelScope.launch { db.noteDao().delete(noteModel.note) }
        DbManager.getInstance().delete(ConvertUtil.convert(noteModel.note))
    }

    //TODO: check syncing of local to remote
    fun getRemoteNotes() {
        remote.getAllNotes { bNotes, error ->
            if (error != null) {
                return@getAllNotes
            }

            viewModelScope.launch {
                val localNotes = db.noteDao().notes()
                val notesToSave = bNotes.map {
                    val localNote = if (it.paperId == null) {
                        db.noteDao().getNote(it.eventId)
                    } else {
                        db.noteDao().getNote(it.eventId, it.paperId)
                    }
                    if (localNote != null) {
                        db.noteDao().delete(localNote)
                    }
                    ConvertUtil.convert(it)
                }
                db.noteDao().insert(notesToSave)
                for (note in localNotes) {
                    remote.addNote(ConvertUtil.convert(note)) { savedNote, _ ->
                        if (savedNote != null) {
                            viewModelScope.launch { db.noteDao().deleteInsert(note, ConvertUtil.convert(savedNote)) }
                        }
                    }
                }
            }
        }
    }
}
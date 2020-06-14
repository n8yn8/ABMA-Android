package com.n8yn8.abma.view

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.NoteEventPaper
import kotlinx.coroutines.launch

class NoteViewModel @ViewModelInject constructor(
        private val db: AppDatabase,
        private val remote: DbManager
) : ViewModel() {

    val notesData: LiveData<List<NoteEventPaper>> = db.noteDao().notesLive

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
                if (notesToSave.isNotEmpty()) {
                    db.noteDao().insert(notesToSave)
                }
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
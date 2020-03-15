package com.n8yn8.abma.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class NoteViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val db: AppDatabase by inject()
    private val remote: DbManager by inject()

    val notesData: LiveData<List<NoteModel>>

    init {
        notesData = Transformations.map(db.noteDao().notesLive) { noteList ->
            noteList.map {
                NoteModel(
                        note = it,
                        event = db.eventDao().getEventById(it.eventId),
                        paper = db.paperDao().getPaperById(it.paperId)
                )
            }
        }
    }

    fun deleteNote(noteModel: NoteModel?) {
        if (noteModel == null) {
            return
        }
        db.noteDao().delete(noteModel.note)
        DbManager.getInstance().delete(ConvertUtil.convert(noteModel.note))
    }

    //TODO: check syncing of local to remote
    fun getRemoteNotes() {
        remote.getAllNotes { bNotes, error ->
            if (error == null) {
                val localNotes = db.noteDao().notes
                val notesToSave = bNotes.map { ConvertUtil.convert(it) }
                db.noteDao().insert(notesToSave)
                for (note in localNotes) {
                    remote.addNote(ConvertUtil.convert(note)) { savedNote, syncError ->
                        if (savedNote != null) {
                            db.noteDao().insert(ConvertUtil.convert(savedNote))
                        }
                    }
                }
            }
        }
    }
}

data class NoteModel(val note: Note, val event: Event?, val paper: Paper?)
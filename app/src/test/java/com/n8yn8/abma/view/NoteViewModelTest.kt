package com.n8yn8.abma.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.ConvertUtil
import com.n8yn8.abma.model.backendless.BNote
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.dao.NoteDao
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.test.util.FakeData
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class NoteViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val remote = mockk<DbManager>()
    private val noteDao = mockk<NoteDao> {
        coEvery {
            delete(any())
            insert(notes = any())
            deleteInsert(any(), any())
        } just Runs
        coEvery { notesLive } returns spyk()
    }
    private val database = mockk<AppDatabase> {
        every { noteDao() } returns this@NoteViewModelTest.noteDao
    }

    private lateinit var noteViewModel: NoteViewModel

    private val event1 = FakeData.getEvent()
    private val paper1 = FakeData.getPaper()
    private val event2 = FakeData.getEvent(index = 2)
    private val paper2 = FakeData.getPaper(index = 2, eventIndex = 2)

    private fun setUpNoteModel() {
        noteViewModel = NoteViewModel(database, remote)
    }

    private fun setUpRemoteResponse(bNotes: List<BNote>) {
        val getNotesCallback = slot<DbManager.OnGetNotesCallback>()
        every {
            remote.getAllNotes(capture(getNotesCallback))
        } answers {
            getNotesCallback.captured.notesRetrieved(bNotes, null)
        }

        val noteToSave = slot<BNote>()
        val saveNotesCallback = slot<DbManager.OnNoteSavedCallback>()
        every {
            remote.addNote(capture(noteToSave), capture(saveNotesCallback))
        } answers {
            val bNote = noteToSave.captured.apply {
                created = Date()
                updated = Date()
                objectId = "$eventId$paperId"
            }
            saveNotesCallback.captured.noteSaved(bNote, null)
        }
    }

    private fun setupDbResponses(dbNotes: List<Note>) {
        coEvery { noteDao.notes() } returns dbNotes
        coEvery { noteDao.getNote(any()) } returns dbNotes.find { note ->
            note.paperId == null
        }
        coEvery { noteDao.getNote(any(), any()) } returns dbNotes.find { note ->
            note.paperId != null
        }
    }

    @Test
    fun getRemoteNotes_nonLocalAllRemote() {
        setUpNoteModel()

        val bNotes = listOf(
                FakeData.getBNote(index = "1", inEventId = event1.objectId),
                FakeData.getBNote(index = "2", inEventId = event1.objectId, inPaperId = paper1.objectId)
        )
        setUpRemoteResponse(bNotes)
        setupDbResponses(emptyList())

        noteViewModel.getRemoteNotes()

        verify(exactly = 0) { remote.addNote(any(), any()) }
        val expectedInserted = bNotes.map { ConvertUtil.convert(it) }
        coVerify { noteDao.insert(expectedInserted) }
    }

    @Test
    fun getRemoteNotes_allLocalNoneRemote() {
        setUpNoteModel()
        val dbNotes = listOf(FakeData.getNote(event1.objectId), FakeData.getNote(event1.objectId, paper1.objectId))
        setupDbResponses(dbNotes)

        setUpRemoteResponse(emptyList())
        noteViewModel.getRemoteNotes()

        //Local added to remote
        verify(exactly = 2) { remote.addNote(any(), any()) }
        coVerify(exactly = 2) { noteDao.deleteInsert(any(), any()) }
    }

    @Test
    fun getRemoteNotes_bothLocalAndRemote() {
        setUpNoteModel()
        val bNotes = listOf(
                FakeData.getBNote(index = "1", inEventId = event2.objectId),
                FakeData.getBNote(index = "2", inEventId = event2.objectId, inPaperId = paper2.objectId)
        )
        setUpRemoteResponse(bNotes)
        val dbNotes = listOf(
                FakeData.getNote(event1.objectId),
                FakeData.getNote(event1.objectId, paper1.objectId)
        )
        setupDbResponses(dbNotes
        )
        noteViewModel.getRemoteNotes()

        coVerify { noteDao.insert(notes = any()) }
        verify(exactly = 2) { remote.addNote(any(), any()) }
    }
}
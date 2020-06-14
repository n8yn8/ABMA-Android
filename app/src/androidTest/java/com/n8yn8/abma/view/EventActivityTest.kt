package com.n8yn8.abma.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.n8yn8.abma.R
import com.n8yn8.abma.di.DataModule
import com.n8yn8.abma.model.AppDatabase
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Note
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.abma.view.adapter.PaperListAdapter
import com.n8yn8.test.util.FakeData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Singleton

@UninstallModules(DataModule::class)
@HiltAndroidTest
@LargeTest
class EventActivityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @get:Rule
    var activityRule = ActivityTestRule(EventActivity::class.java, true, false)

    @InstallIn(ApplicationComponent::class)
    @Module
    object TestModule {
        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()

        }

        @Provides
        @Singleton
        fun provideDbManager(): DbManager {
            return mockk()
        }

        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        }
    }

    @Inject
    lateinit var database: AppDatabase

    @Before
    @Throws(Exception::class)
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
        }
    }

    private fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View?>): Matcher<View?>? {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                        ?: // has no item on such position
                        return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    @Test
    fun testEvent() {

        activityRule.launchActivity(Intent().putExtra("event_id", "event1"))

        val event = FakeData.getEvent()

        checkEventUi(event)

//        onData(
//                withId(R.id.papersListView)
//        ).check(
//                doesNotExist()
//        )
    }

    @Test
    fun testEventWithPapers() {
        val event = FakeData.getEvent()
        val paper1 = FakeData.getPaper(1)
        val paper2 = FakeData.getPaper(2)
        runBlocking {
            database.paperDao().insert(arrayListOf(paper1, paper2))
        }

        activityRule.launchActivity(Intent().putExtra("event_id", "event1"))

        checkEventUi(event)

        checkInPaperRecycler(0, R.id.paperTitleTextView, paper1.title)
        checkInPaperRecycler(0, R.id.paperAuthorTextView, paper1.author)
        checkInPaperRecycler(1, R.id.paperTitleTextView, paper2.title)
        checkInPaperRecycler(1, R.id.paperAuthorTextView, paper2.author)

    }

    private fun checkInPaperRecycler(position: Int, viewId: Int, text: String) {
        onView(
                withId(R.id.papersListView)
        ).check(
                matches(
                        atPosition(position, hasDescendant(
                                allOf(
                                        withId(viewId),
                                        withText(text)
                                )
                        ))
                )
        )
    }

    @Test
    fun testPaper() {
        val event = FakeData.getEvent()
        val paper1 = FakeData.getPaper(1)
        val paper2 = FakeData.getPaper(2)
        runBlocking {
            database.paperDao().insert(arrayListOf(paper1, paper2))
        }

        activityRule.launchActivity(Intent().putExtra("event_id", "event1"))

        checkEventUi(event)

        onView(
                withId(R.id.papersListView)
        ).perform(
                actionOnItemAtPosition<PaperListAdapter.ViewHolder>(0, click())
        )

        checkEventUi(event, paper1)
    }

    @Test
    fun testEventWithNote() {
        val event = FakeData.getEvent()
        val note = FakeData.getNote(event.objectId)

        runBlocking {
            database.noteDao().insert(note)
        }

        testEvent()

        onView(
                withId(R.id.noteEditText)
        ).check(
                matches(withText(note.content))
        )

    }

    @Test
    fun testPaperWithNote() {
        val event = FakeData.getEvent()
        val paper = FakeData.getPaper(1)
        val note = FakeData.getNote(event.objectId, paper.objectId)
        runBlocking {
            database.paperDao().insert(paper)
            database.noteDao().insert(note)
        }
        testPaper()
    }

    @Test
    fun testSaveNoteForEvent() {
        testEvent()

        val noteText = "Text to save for this event test"

        onView(withId(R.id.noteEditText)).perform(typeText(noteText))
        onView(withId(R.id.saveNoteButton)).perform(click())

        var savedNote: Note? = null
        runBlocking { savedNote = database.noteDao().getNote(FakeData.getEvent().objectId) }
        Assert.assertEquals(noteText, savedNote!!.content)
    }

    @Test
    fun testSaveNoteForPaper() {
        testPaper()

        val noteText = "Text to save for this paper test"

        onView(withId(R.id.noteEditText)).perform(typeText(noteText))
        onView(withId(R.id.saveNoteButton)).perform(click())

        var savedNote: Note? = null
        runBlocking { savedNote = database.noteDao().getNote(FakeData.getEvent().objectId, FakeData.getPaper(1).objectId) }

        Assert.assertEquals(noteText, savedNote!!.content)
    }

    private fun checkEventUi(event: Event, paper: Paper? = null) {
        onView(
                withId(R.id.dayTextView)
        ).check(
                matches(withText("MONDAY"))
        )

        onView(
                withId(R.id.dateTextView)
        ).check(
                matches(withText("1"))
        )

        val title = paper?.title ?: event.title
        onView(
                withId(R.id.titleTextView)
        ).check(
                matches(withText(title))
        )

        val subtitle = paper?.author ?: event.subtitle
        onView(
                withId(R.id.subtitleTextView)
        ).check(
                matches(withText(subtitle))
        )

        val details = paper?.synopsis ?: event.details
        onView(
                withId(R.id.detailTextView)
        ).check(
                matches(withText(details))
        )

        onView(
                withId(R.id.timeTextView)
        ).check(
                matches(withText("1:00 AM - 1:00 AM"))
        )

        onView(
                withId(R.id.placeTextView)
        ).check(
                matches(withText(event.place))
        )

        var note: Note? = null
        runBlocking {
            note = if (paper == null) {
                database.noteDao().getNote(event.objectId)
            } else {
                database.noteDao().getNote(event.objectId, paper.objectId)
            }
        }

        onView(
                withId(R.id.noteEditText)
        ).check(
                matches(withText(note?.content ?: ""))
        )
    }
}
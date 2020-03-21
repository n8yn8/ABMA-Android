package com.n8yn8.abma.view

import android.content.Intent
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.n8yn8.abma.BaseTest
import com.n8yn8.abma.R
import com.n8yn8.abma.model.entities.Event
import com.n8yn8.abma.model.entities.Paper
import com.n8yn8.test.util.FakeData
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EventActivityTest: BaseTest() {
    @get:Rule
    var activityRule = ActivityTestRule(EventActivity::class.java, true, false)

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        runBlocking {
            database.yearDao().insert(FakeData.getYear())
            database.eventDao().insert(FakeData.getEvent())
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

        onData(
                Matchers.anything()
        ).inAdapterView(withId(R.id.papersListView))
                .atPosition(0)
                .onChildView(withId(R.id.paperTitleTextView))
                .check(matches(withText(paper1.title)))

        onData(
                Matchers.anything()
        ).inAdapterView(withId(R.id.papersListView))
                .atPosition(0)
                .onChildView(withId(R.id.paperAuthorTextView))
                .check(matches(withText(paper1.author)))

        onData(
                Matchers.anything()
        ).inAdapterView(withId(R.id.papersListView))
                .atPosition(1)
                .onChildView(withId(R.id.paperTitleTextView))
                .check(matches(withText(paper2.title)))

        onData(
                Matchers.anything()
        ).inAdapterView(withId(R.id.papersListView))
                .atPosition(1)
                .onChildView(withId(R.id.paperAuthorTextView))
                .check(matches(withText(paper2.author)))

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

        onData(
                Matchers.anything()
        ).inAdapterView(withId(R.id.papersListView))
                .atPosition(0)
                .perform(ViewActions.click())

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

        onView(withId(R.id.noteEditText)).perform(ViewActions.typeText(noteText))
        onView(withId(R.id.saveNoteButton)).perform(ViewActions.click())

        val savedNote = database.noteDao().getNote(FakeData.getEvent().objectId)

        Assert.assertEquals(noteText, savedNote!!.content)
    }

    @Test
    fun testSaveNoteForPaper() {
        testPaper()

        val noteText = "Text to save for this paper test"

        onView(withId(R.id.noteEditText)).perform(ViewActions.typeText(noteText))
        onView(withId(R.id.saveNoteButton)).perform(ViewActions.click())

        val savedNote = database.noteDao().getNote(FakeData.getEvent().objectId, FakeData.getPaper(1).objectId)

        Assert.assertEquals(noteText, savedNote!!.content)
    }

    private fun checkEventUi(event: Event, paper: Paper? = null) {
        onView(
                withId(R.id.dayTextView)
        ).check(
                matches(withText("FRIDAY"))
        )

        onView(
                withId(R.id.dateTextView)
        ).check(
                matches(withText("20"))
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
                matches(withText("4:20 AM - 4:40 AM"))
        )

        onView(
                withId(R.id.placeTextView)
        ).check(
                matches(withText(event.place))
        )

        val note = if (paper == null) {
            database.noteDao().getNote(event.objectId)
        } else {
            database.noteDao().getNote(event.objectId, paper.objectId)
        }

        onView(
                withId(R.id.noteEditText)
        ).check(
                matches(withText(note?.content ?: ""))
        )
    }
}
package com.n8yn8.abma.model;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Year;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest {

    AppDatabase database;

    @Before
    public void setUp() throws Exception {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(), AppDatabase.class).build();
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }

    @Test
    public void testInsert() {

        Year year = getYear();

        Event event = getEvent(year.objectId);

        database.yearDao().insert(year);
        database.eventDao().insert(event);

        List<Year> years = database.yearDao().getYears();
        Assert.assertEquals(years.size(), 1);

        List<Event> events = database.eventDao().getEvents(years.get(0).objectId);
        Assert.assertEquals(events.size(), 1);

        Assert.assertEquals(year, years.get(0));
        Assert.assertEquals(event, events.get(0));

    }

    public Year getYear() {
        Year year = new Year();
        year.id = 1;
        year.info = "info";
        year.name = 2016;
        year.objectId = "id1";
        year.welcome = "welcome";

        return year;
    }

    public static Event getEvent(String yearObjectId) {
        Event event = new Event();
        event.details = "details";
        event.endDate = 5678L;
        event.startDate = 1234L;
        event.id = 1;
        event.objectId = "id1";
        event.place = "place";
        event.title = "title";
        event.subtitle = "subtitle";
        event.yearId = yearObjectId;

        return event;
    }
}
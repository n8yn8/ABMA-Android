package com.n8yn8.abma;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.n8yn8.abma.model.AppDatabase;
import com.n8yn8.abma.model.AppDatabaseTest;
import com.n8yn8.abma.model.backendless.BEvent;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Year;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {

    @Test
    public void getStartOfDay() {

        long dateStart = Utils.getStartOfDay(1554508800000L);
        Assert.assertEquals(1554508800000L, dateStart);

        long dateStart2 = Utils.getStartOfDay(1554508800001L);
        Assert.assertEquals(1554508800000L, dateStart2);

        long dateMillis = Utils.getStartOfDay(1554552000000L);
        Assert.assertEquals(1554508800000L, dateMillis);
    }

    @Test
    public void testDeleteEvents() {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());

        String yearId = "yearId";
        Year year = new Year();
        year.objectId = yearId;
        db.yearDao().insert(year);

        List<BEvent> remoteEvents = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            Event event = AppDatabaseTest.getEvent(yearId);
            event.objectId = String.valueOf(i);

            db.eventDao().insert(event);

            if (i % 2 == 0) {
                BEvent bEvent = new BEvent();
                bEvent.setObjectId(String.valueOf(i));
                bEvent.setDetails(event.details);
                bEvent.setEndDate(new Date(event.endDate));
                bEvent.setLocation(event.place);
                bEvent.setStartDate(new Date(event.startDate));
                remoteEvents.add(bEvent);
            }
        }

        Utils.saveEvents(db, yearId, remoteEvents);

        List<Event> savedEvents = db.eventDao().getEvents(yearId);

        Assert.assertEquals(4, savedEvents.size());

        for (Event savedEvent : savedEvents) {
            int objectId = Integer.parseInt(savedEvent.objectId);
            Assert.assertEquals(0, objectId % 2);
        }
    }
}
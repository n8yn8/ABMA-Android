package com.n8yn8.abma.model.old;

import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Nate on 2/15/15.
 */
public class Schedule extends Object {

    private final String TAG = "Schedule";

    private Map<String, ArrayList<Event>> schedule;
    private List<String> eventDays;
    private List<Date> eventDates;

    private int eventIndex;
    private int dayIndex;
    private int paperIndex;

    public Schedule(NSDictionary scheduleDict) {

        if (schedule == null) {
            eventDays = new ArrayList<>(scheduleDict.keySet());
            eventDates = new ArrayList<>();
            dayIndex = 0;
            eventIndex = 0;
            paperIndex = -1;
            schedule = new HashMap<>();
            for (String dayKey : eventDays) {
                int index = 0;
                NSObject[] dayNSArray = ((NSArray) scheduleDict.objectForKey(dayKey)).getArray();
                ArrayList<Event> day = new ArrayList<>();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("LLL d, y");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = dateFormat.parse(dayKey);
                    eventDates.add(date);
                } catch (ParseException ex) {
                    Log.e(TAG, "Error parsing date, " + ex.getLocalizedMessage());
                }
                for (NSObject eventNSObject : dayNSArray) {
                    Event event = new Event(eventNSObject);
                    day.add(event);
                    event.setIndex(index);
                    index++;
                }
                schedule.put(dayKey, day);
            }
        }

    }


    public List<String> getEventDays() {
        return eventDays;
    }

    public ArrayList<Event> getNextDay() {
        if (dayIndex == eventDays.size() - 1) {
            return null;
        } else {
            dayIndex++;
            return schedule.get(eventDays.get(dayIndex));
        }
    }

    public ArrayList<Event> getPrevDay() {
        if (dayIndex == 0) {
            return null;
        } else {
            dayIndex--;
            return schedule.get(eventDays.get(dayIndex));
        }
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public ArrayList<Event> getDay(String dayIndex) {
        return schedule.get(dayIndex);
    }

    public ArrayList<Event> getCurrentDay() {
        return schedule.get(eventDays.get(dayIndex));
    }

    public String getCurrentDateString() {
        return eventDays.get(dayIndex);
    }

    public Date getCurrentDate() {
        return eventDates.get(dayIndex);
    }

    public void setCurrentEventIndex(int eventIndex) {
        this.eventIndex = eventIndex;
    }


    public Event getEventByIndex(int index) {
        int daySize = 0;
        int dayPosition = 0;
        Event returnEvent = null;
        while (returnEvent == null) {
            ArrayList<Event> day = schedule.get(eventDays.get(dayPosition));
            daySize = day.size();
            if (index > daySize) {
                index = index - daySize;
                dayPosition++;
            } else {
                returnEvent = day.get(index);
            }
        }
        return returnEvent;
    }

    public Event getCurrentEvent() {
        String thisDate = eventDays.get(dayIndex);
        List<Event> thisDay = schedule.get(thisDate);
        Event thisEvent = thisDay.get(eventIndex);
        return thisEvent;
    }

    public Event getPrevEvent() {
        ArrayList<Event> day = schedule.get(eventDays.get(dayIndex));
        if (eventIndex == 0) { //First event of day
            day = getPrevDay();
            if (day != null) { //Prev day
                eventIndex = day.size() - 1; //Last event of prev day
                return day.get(eventIndex);
            } else { //First day of schedule reached
                return null;
            }
        } else {
            eventIndex--;
            return day.get(eventIndex);
        }
    }

    public Event getNextEvent() {
        ArrayList<Event> day = schedule.get(eventDays.get(dayIndex));
        if (eventIndex == day.size() - 1) { //Last event of day
            day = getNextDay();
            if (day != null) { //Next day
                eventIndex = 0;
                return day.get(eventIndex);
            } else { //Last day of schedule reached
                return null;
            }
        } else {
            eventIndex++;
            return day.get(eventIndex);
        }
    }

    public int getPaperIndex() {
        return paperIndex;
    }

    public void setPaperIndex(int paperIndex) {
        this.paperIndex = paperIndex;
    }

    public Paper getCurrentPaper() {
        if (paperIndex == -1) {
            return null;
        } else {
            Event thisEvent = getCurrentEvent();
            List<Paper> thesePapers = thisEvent.getPapers();
            Paper thisPaper = thesePapers.get(paperIndex);
            return thisPaper;
        }
    }

    public Paper getPrevPaper() {
        List<Paper> papers = getCurrentEvent().getPapers();
        if (paperIndex == 0) { //First paper of event
            return null;
        } else {
            paperIndex--;
            return papers.get(paperIndex);
        }
    }

    public Paper getNextPaper() {
        List<Paper> papers = getCurrentEvent().getPapers();
        if (paperIndex == papers.size() - 1) { //Last paper of event
            return null;
        } else {
            paperIndex++;
            return papers.get(paperIndex);
        }
    }
}

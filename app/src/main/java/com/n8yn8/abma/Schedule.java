package com.n8yn8.abma;

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

/**
 * Created by Nate on 2/15/15.
 */
public class Schedule extends Object{

    private final String TAG = "Schedule";

    private Map<String, ArrayList<Event>> schedule;
    private List<String> eventDays;
    private List<Date> eventDates;

    private int eventIndex;
    private int dayIndex;

    public Schedule(NSDictionary scheduleDict) {

        if (schedule == null) {
            eventDays = new ArrayList<>(scheduleDict.keySet());
            eventDates = new ArrayList<>();
            dayIndex = 0;
            eventIndex = 0;
            schedule = new HashMap<>();
            int index = 0;
            for (String dayKey: eventDays) {
                NSObject[] dayNSArray = ((NSArray) scheduleDict.objectForKey(dayKey)).getArray();
                ArrayList<Event> day = new ArrayList<>();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("LLL d, y");
                    Date date = dateFormat.parse(dayKey);
                    eventDates.add(date);
                } catch (ParseException ex) {
                    Log.e(TAG, "Error parsing date, "+ex.getLocalizedMessage());
                }
                for(NSObject eventNSObject: dayNSArray) {
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
        if (dayIndex == eventDays.size()-1) {
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

    public ArrayList<Event> getDay(String dayIndex){
        return schedule.get(dayIndex);
    }

    public ArrayList<Event> getCurrentDay() {
        return schedule.get(eventDays.get(dayIndex));
    }

    public String getCurrentDateString(){
        return eventDays.get(dayIndex);
    }

    public Date getCurrentDate() {
        return eventDates.get(dayIndex);
    }

    public void setCurrentEventIndex(int eventIndex) {
        this.eventIndex = eventIndex;
    }

    public Event getCurrentEvent() {
        return schedule.get(eventDays.get(dayIndex)).get(eventIndex);
    }

    public Event getPrevEvent() {
        ArrayList<Event> day = schedule.get(eventDays.get(dayIndex));
        if (eventIndex == 0) { //First event of day
            day = getPrevDay();
            if (day != null) { //Prev day
                eventIndex = day.size()-1; //Last event of prev day
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
}

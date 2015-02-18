package com.n8yn8.abma;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;

import java.util.ArrayList;
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
    private ArrayList<Event> day;
    private int eventIndex;
    private int dayIndex;

    public Schedule(NSDictionary scheduleDict) {
        eventDays = new ArrayList<>(scheduleDict.keySet());
        dayIndex = 0;
        eventIndex = 0;
        schedule = new HashMap<>();
        for (String dayKey: eventDays) {
            NSObject[] dayNSArray = ((NSArray) scheduleDict.objectForKey(dayKey)).getArray();
            day = new ArrayList<>();
            for(NSObject eventNSObject: dayNSArray) {
                Event event = new Event(eventNSObject);
                day.add(event);
            }
            schedule.put(dayKey, day);
        }

    }

    public List<String> getEventDays() {
        return eventDays;
    }

    public ArrayList<Event> getNextDay() {
        dayIndex++;
        return schedule.get(eventDays.get(dayIndex));
    }

    public ArrayList<Event> getPrevDay() {
        dayIndex--;
        return schedule.get(eventDays.get(dayIndex));
    }

    public ArrayList<Event> getDay(String dayIndex){
        return schedule.get(dayIndex);
    }

    public ArrayList<Event> getCurrentDay() {
        return schedule.get(eventDays.get(dayIndex));
    }

    public String getCurrentDate(){
        return eventDays.get(dayIndex);
    }

    public void setCurrentEventIndex(int dayIndex, int eventIndex) {
        this.eventIndex = eventIndex;
        this.dayIndex = dayIndex;
    }

    public Event getNextEvent() {
        return day.get(eventIndex);
    }
}

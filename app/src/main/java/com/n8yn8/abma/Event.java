package com.n8yn8.abma;

import com.dd.plist.NSObject;

import java.util.Map;

/**
 * Created by Nate on 2/15/15.
 */
public class Event {

    private final String TAG = "Event";

    private String title;
    private String subtitle;
    private String time;
    private String place;
    private String details;

    public Event(NSObject eventNSObject) {
        Map<String, String> eventMap = (Map<String, String>) eventNSObject.toJavaObject();
        this.title = eventMap.get("Title");
        this.subtitle = eventMap.get("Subtitle");
        this.time = eventMap.get("Time");
        this.place = eventMap.get("Location");
        this.details = eventMap.get("Description");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}

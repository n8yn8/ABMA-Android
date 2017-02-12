package com.n8yn8.abma.model;

/**
 * Created by Nate on 3/14/15.
 */
public class Note {
    private final String TAG = "Note";

    private int id;
    private int dayId;
    private int eventId;
    private int paperId;
    private String content;
    private String eventName;

    public Note(int id, int dayId, int eventId, int paperId, String content, String eventName) {
        this.id = id;
        this.dayId = dayId;
        this.eventId = eventId;
        this.paperId = paperId;
        this.content = content;
        this.eventName = eventName;
    }

    public Note(int dayId, int eventId, int paperId, String content, String eventName) {
        this.dayId = dayId;
        this.eventId = eventId;
        this.paperId = paperId;
        this.content = content;
        this.eventName = eventName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDayId() {
        return dayId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

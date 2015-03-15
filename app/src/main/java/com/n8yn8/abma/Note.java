package com.n8yn8.abma;

/**
 * Created by Nate on 3/14/15.
 */
public class Note {
    private final String TAG = "Note";

    private int id;
    private int eventId;
    private String content;

    public Note(int id, int eventId, String content) {
        this.id = id;
        this.eventId = eventId;
        this.content = content;
    }

    public Note(int eventId, String content) {
        this.eventId = eventId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
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
}

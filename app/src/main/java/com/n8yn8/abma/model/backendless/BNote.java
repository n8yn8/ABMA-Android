package com.n8yn8.abma.model.backendless;

import com.backendless.BackendlessUser;

/**
 * Created by Nate on 2/20/17.
 */

public class BNote {

    String objectId;
    String content;
    String paperId;
    String eventId;
    BackendlessUser user;

    public BNote() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public BackendlessUser getUser() {
        return user;
    }

    public void setUser(BackendlessUser user) {
        this.user = user;
    }
}

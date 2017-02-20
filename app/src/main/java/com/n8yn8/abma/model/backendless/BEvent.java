package com.n8yn8.abma.model.backendless;

import java.util.Date;
import java.util.List;

/**
 * Created by Nate on 2/20/17.
 */

public class BEvent {

    String objectId;
    String details;
    Date endDate;
    String location;
    Date startDate;
    String subtitle;
    String title;
    List<BPaper> papers;

    public BEvent() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BPaper> getPapers() {
        return papers;
    }

    public void setPapers(List<BPaper> papers) {
        this.papers = papers;
    }
}

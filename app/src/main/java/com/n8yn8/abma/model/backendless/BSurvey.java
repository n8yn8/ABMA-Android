package com.n8yn8.abma.model.backendless;

import java.util.Date;

/**
 * Created by Nate on 3/22/18.
 */

public class BSurvey {

    private String title = "";
    private String details = "";
    private String url = "";
    private Date start = new Date();
    private Date end = new Date();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}

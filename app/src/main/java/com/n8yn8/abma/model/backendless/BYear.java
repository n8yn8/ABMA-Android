package com.n8yn8.abma.model.backendless;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by Nate on 2/19/17.
 */

public class BYear {

    String objectId;
    int name;
    List<BEvent> events;
    String welcome;
    String info;
    List<BSponsor> sponsors;
    String surveys;
    String maps;

    public BYear() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public List<BEvent> getEvents() {
        return events;
    }

    public void setEvents(List<BEvent> events) {
        this.events = events;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<BSponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<BSponsor> sponsors) {
        this.sponsors = sponsors;
    }

    @Nullable
    public String getSurveys() {
        return surveys;
    }

    public void setSurveys(String surveys) {
        this.surveys = surveys;
    }

    public String getMaps() {
        return maps;
    }

    public void setMaps(String maps) {
        this.maps = maps;
    }
}

package com.n8yn8.abma.model;

import java.util.Date;

/**
 * Created by Nate on 4/7/17.
 */

public class Survey {

    String surveyUrl;
    Date surveyStart;
    Date surveyEnd;

    public String getSurveyUrl() {
        return surveyUrl;
    }

    public void setSurveyUrl(String surveyUrl) {
        this.surveyUrl = surveyUrl;
    }

    public Date getSurveyStart() {
        return surveyStart;
    }

    public void setSurveyStart(Date surveyStart) {
        this.surveyStart = surveyStart;
    }

    public Date getSurveyEnd() {
        return surveyEnd;
    }

    public void setSurveyEnd(Date surveyEnd) {
        this.surveyEnd = surveyEnd;
    }
}

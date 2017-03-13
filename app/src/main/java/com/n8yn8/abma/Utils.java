package com.n8yn8.abma;

import com.n8yn8.abma.model.backendless.BEvent;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Nate on 2/27/17.
 */

public class Utils {

    public static String getTimes(BEvent event) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String times = timeFormatter.format(event.getStartDate());
        if (event.getEndDate() != null) {
            times += " - " + timeFormatter.format(event.getEndDate());
        }
        return times;
    }
}

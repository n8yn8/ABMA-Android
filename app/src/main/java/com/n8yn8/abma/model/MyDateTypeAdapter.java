package com.n8yn8.abma.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Nate on 3/25/18.
 */

public class MyDateTypeAdapter extends TypeAdapter<Date> {

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null)
            out.nullValue();
        else
            out.value(value.getTime());
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in != null)
            return new Date(in.nextLong());
        else
            return null;
    }
}

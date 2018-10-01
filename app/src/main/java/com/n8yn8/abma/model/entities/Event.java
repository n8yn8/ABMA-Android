package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.Objects;

@Entity(tableName = DatabaseHandler.TABLE_EVENTS,
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                childColumns = DatabaseHandler.KEY_YEAR_ID),
        indices = {@Index(value = {DatabaseHandler.KEY_OBJECT_ID}, unique = true),
                @Index(value = DatabaseHandler.KEY_YEAR_ID)})
public class Event {

//    private static String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_OBJECT_ID + " STRING,"
//            + KEY_YEAR_ID + " STRING,"
//            + KEY_DETAILS + " TEXT,"
//            + KEY_END_DATE + " INT,"
//            + KEY_START_DATE + " INT,"
//            + KEY_PLACE + " STRING,"
//            + KEY_TITLE + " STRING,"
//            + KEY_SUBTITLE + " STRING,"
//            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
//            + ")";

    @PrimaryKey
    public int id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_YEAR_ID)
    public String yearId;

    @ColumnInfo(name = DatabaseHandler.KEY_DETAILS)
    public String details;

    @ColumnInfo(name = DatabaseHandler.KEY_END_DATE)
    public long endDate;

    @ColumnInfo(name = DatabaseHandler.KEY_START_DATE)
    public long startDate;

    @ColumnInfo(name = DatabaseHandler.KEY_PLACE)
    public String place;

    @ColumnInfo(name = DatabaseHandler.KEY_TITLE)
    public String title;

    @ColumnInfo(name = DatabaseHandler.KEY_SUBTITLE)
    public String subtitle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
                endDate == event.endDate &&
                startDate == event.startDate &&
                Objects.equals(objectId, event.objectId) &&
                Objects.equals(yearId, event.yearId) &&
                Objects.equals(details, event.details) &&
                Objects.equals(place, event.place) &&
                Objects.equals(title, event.title) &&
                Objects.equals(subtitle, event.subtitle);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, objectId, yearId, details, endDate, startDate, place, title, subtitle);
    }
}

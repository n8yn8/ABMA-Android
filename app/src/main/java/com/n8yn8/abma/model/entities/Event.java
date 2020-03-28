package com.n8yn8.abma.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "events",
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = "object_id",
                childColumns = "year_id"),
        indices = {@Index(value = {"object_id"}, unique = true),
                @Index(value = "year_id")})
public class Event {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "object_id")
    public String objectId;

    @ColumnInfo(name = "year_id")
    public String yearId;

    @ColumnInfo(name = "details")
    public String details;

    @ColumnInfo(name = "end_date")
    public Long endDate;

    @ColumnInfo(name = "start_date")
    public Long startDate;

    @ColumnInfo(name = "place")
    public String place;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "subtitle")
    public String subtitle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(objectId, event.objectId) &&
                Objects.equals(yearId, event.yearId) &&
                Objects.equals(details, event.details) &&
                Objects.equals(endDate, event.endDate) &&
                Objects.equals(startDate, event.startDate) &&
                Objects.equals(place, event.place) &&
                Objects.equals(title, event.title) &&
                Objects.equals(subtitle, event.subtitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectId, yearId, details, endDate, startDate, place, title, subtitle);
    }

    @Override
    @NonNull
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", objectId='" + objectId + '\'' +
                ", yearId='" + yearId + '\'' +
                ", details='" + details + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", place='" + place + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                '}';
    }
}

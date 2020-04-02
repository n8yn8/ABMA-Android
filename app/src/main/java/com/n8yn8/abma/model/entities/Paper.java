package com.n8yn8.abma.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "papers",
        foreignKeys = @ForeignKey(
                entity = Event.class,
                parentColumns = "object_id",
                childColumns = "event_id"),
        indices = {@Index(value = {"object_id"}, unique = true),
                @Index(value = "event_id")})
public class Paper {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "object_id")
    public String objectId;

    @ColumnInfo(name = "event_id")
    public String eventId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "synopsis")
    public String synopsis;

    @ColumnInfo(name = "order_by")
    public Integer order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paper paper = (Paper) o;
        return Objects.equals(id, paper.id) &&
                Objects.equals(objectId, paper.objectId) &&
                Objects.equals(eventId, paper.eventId) &&
                Objects.equals(title, paper.title) &&
                Objects.equals(author, paper.author) &&
                Objects.equals(synopsis, paper.synopsis) &&
                Objects.equals(order, paper.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectId, eventId, title, author, synopsis, order);
    }

    @Override
    @NonNull
    public String toString() {
        return "Paper{" +
                "id=" + id +
                ", objectId='" + objectId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", order=" + order +
                '}';
    }
}

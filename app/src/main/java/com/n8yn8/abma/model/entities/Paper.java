package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.Objects;

@Entity(tableName = DatabaseHandler.TABLE_PAPERS,
        foreignKeys = @ForeignKey(
                entity = Event.class,
                parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                childColumns = DatabaseHandler.KEY_EVENT_ID),
        indices = {@Index(value = {DatabaseHandler.KEY_OBJECT_ID}, unique = true),
                @Index(value = DatabaseHandler.KEY_EVENT_ID)})
public class Paper {

//    private static String CREATE_PAPERS_TABLE = "CREATE TABLE " + TABLE_PAPERS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_OBJECT_ID + " STRING,"
//            + KEY_EVENT_ID + " STRING,"
//            + KEY_TITLE + " STRING,"
//            + KEY_AUTHOR + " STRING,"
//            + KEY_SYNOPSIS + " TEXT,"
//            + KEY_ORDER + " INT,"
//            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
//            + ")";

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_EVENT_ID)
    public String eventId;

    @ColumnInfo(name = DatabaseHandler.KEY_TITLE)
    public String title;

    @ColumnInfo(name = DatabaseHandler.KEY_AUTHOR)
    public String author;

    @ColumnInfo(name = DatabaseHandler.KEY_SYNOPSIS)
    public String synopsis;

    @ColumnInfo(name = DatabaseHandler.KEY_ORDER)
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
}

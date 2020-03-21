package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.Objects;

@Entity(tableName = DatabaseHandler.TABLE_NOTES,
        foreignKeys = {
            @ForeignKey(
                    entity = Event.class,
                    parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                    childColumns = DatabaseHandler.KEY_EVENT_ID),
            @ForeignKey(
                    entity = Paper.class,
                    parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                    childColumns = DatabaseHandler.KEY_PAPER_ID)},
        indices = {@Index(value = {DatabaseHandler.KEY_OBJECT_ID}, unique = true),
                @Index(value = DatabaseHandler.KEY_EVENT_ID),
                @Index(value = DatabaseHandler.KEY_PAPER_ID)})
public class Note {

//    "CREATE TABLE " + TABLE_NOTES + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_OBJECT_ID + " STRING,"
//            + KEY_EVENT_ID + " STRING,"
//            + KEY_PAPER_ID + " STRING,"
//            + KEY_NOTE_CONTENT + " TEXT,"
//            + KEY_CREATED + " INT,"
//            + KEY_UPDATED + " INT,"
//            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
//            + ")"

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_EVENT_ID)
    public String eventId;

    @ColumnInfo(name = DatabaseHandler.KEY_PAPER_ID)
    public String paperId;

    @ColumnInfo(name = DatabaseHandler.KEY_NOTE_CONTENT)
    public String content;

    @ColumnInfo(name = DatabaseHandler.KEY_CREATED)
    @Nullable
    public Long created;

    @ColumnInfo(name = DatabaseHandler.KEY_UPDATED)
    @Nullable
    public Long updated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) &&
                Objects.equals(objectId, note.objectId) &&
                eventId.equals(note.eventId) &&
                Objects.equals(paperId, note.paperId) &&
                content.equals(note.content) &&
                Objects.equals(created, note.created) &&
                Objects.equals(updated, note.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectId, eventId, paperId, content, created, updated);
    }
}

package com.n8yn8.abma.model.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "notes",
        foreignKeys = {
                @ForeignKey(
                        entity = Event.class,
                        parentColumns = "object_id",
                        childColumns = "event_id"),
                @ForeignKey(
                        entity = Paper.class,
                        parentColumns = "object_id",
                        childColumns = "paper_id")},
        indices = {@Index(value = {"object_id"}, unique = true),
                @Index(value = "event_id"),
                @Index(value = "paper_id")})
public class Note {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "object_id")
    public String objectId;

    @ColumnInfo(name = "event_id")
    public String eventId;

    @ColumnInfo(name = "paper_id")
    public String paperId;

    @ColumnInfo(name = "note_content")
    public String content;

    @ColumnInfo(name = "created_at")
    @Nullable
    public Long created;

    @ColumnInfo(name = "updated_at")
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

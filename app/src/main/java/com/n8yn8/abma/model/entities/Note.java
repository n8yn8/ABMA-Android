package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.n8yn8.abma.model.old.DatabaseHandler;

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
}

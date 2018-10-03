package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

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
}

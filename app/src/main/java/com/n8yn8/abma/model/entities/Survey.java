package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

@Entity(tableName = DatabaseHandler.TABLE_SURVEYS,
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                childColumns = DatabaseHandler.KEY_YEAR_ID),
        indices = {@Index(value = {DatabaseHandler.KEY_URL}, unique = true),
                @Index(value = DatabaseHandler.KEY_YEAR_ID)})
public class Survey {

//    "CREATE TABLE " + TABLE_SURVEYS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_YEAR_ID + " STRING,"
//            + KEY_TITLE + " STRING,"
//            + KEY_DETAILS + " STRING,"
//            + KEY_URL + " TEXT,"
//            + KEY_SURVEY_START + " INTEGER,"
//            + KEY_SURVEY_END + " INTEGER,"
//            + "UNIQUE (" + KEY_URL + ") ON CONFLICT REPLACE"
//            + ")"

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_YEAR_ID)
    public String yearId;

    @ColumnInfo(name = DatabaseHandler.KEY_TITLE)
    public String title;

    @ColumnInfo(name = DatabaseHandler.KEY_DETAILS)
    public String details;

    @ColumnInfo(name = DatabaseHandler.KEY_URL)
    public String url;

    @ColumnInfo(name = DatabaseHandler.KEY_SURVEY_START)
    public Long startDate;

    @ColumnInfo(name = DatabaseHandler.KEY_SURVEY_END)
    public Long endDate;
}

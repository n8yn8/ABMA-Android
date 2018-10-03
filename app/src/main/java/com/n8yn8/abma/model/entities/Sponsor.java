package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

@Entity(tableName = DatabaseHandler.TABLE_SPONSORS,
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                childColumns = DatabaseHandler.KEY_YEAR_ID),
        indices = {@Index(value = {DatabaseHandler.KEY_OBJECT_ID}, unique = true),
                @Index(value = DatabaseHandler.KEY_YEAR_ID)})
public class Sponsor {

//    "CREATE TABLE " + TABLE_SPONSORS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_OBJECT_ID + " STRING,"
//            + KEY_YEAR_ID + " STRING,"
//            + KEY_IMAGE_URL + " STRING,"
//            + KEY_URL + " STRING,"
//            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
//            + ")";

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_YEAR_ID)
    public String yearId;

    @ColumnInfo(name = DatabaseHandler.KEY_IMAGE_URL)
    public String imageUrl;

    @ColumnInfo(name = DatabaseHandler.KEY_URL)
    public String url;
}

package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

@Entity(tableName = DatabaseHandler.TABLE_YEARS,
        indices = {@Index(value = {DatabaseHandler.KEY_OBJECT_ID}, unique = true)})
public class Year {

//    private String CREATE_YEARS_TABLE = "CREATE TABLE " + TABLE_YEARS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_OBJECT_ID + " STRING,"
//            + KEY_NAME + " INTEGER,"
//            + KEY_INFO + " TEXT,"
//            + KEY_WELCOME + " TEXT,"
//            + KEY_SURVEY_URL + " TEXT,"
//            + KEY_SURVEY_START + " INT,"
//            + KEY_SURVEY_END + " INT,"
//            + "UNIQUE (" + KEY_OBJECT_ID + ") ON CONFLICT REPLACE"
//            + ")";

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_NAME)
    public Integer name;

//    List<BEvent> events;

    @ColumnInfo(name = DatabaseHandler.KEY_WELCOME)
    public String welcome;

    @ColumnInfo(name = DatabaseHandler.KEY_INFO)
    public String info;

//    List<BSponsor> sponsors;

//    String maps;

    @Override
    public String toString() {
        return "Year{" +
                "id=" + id +
                ", objectId='" + objectId + '\'' +
                ", name=" + name +
                ", welcome='" + welcome + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}

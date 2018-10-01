package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

import java.util.Objects;

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
    public int id;

    @ColumnInfo(name = DatabaseHandler.KEY_OBJECT_ID)
    public String objectId;

    @ColumnInfo(name = DatabaseHandler.KEY_NAME)
    public int name;

//    List<BEvent> events;

    @ColumnInfo(name = DatabaseHandler.KEY_WELCOME)
    public String welcome;

    @ColumnInfo(name = DatabaseHandler.KEY_INFO)
    public String info;

//    List<BSponsor> sponsors;

    @ColumnInfo(name = DatabaseHandler.TABLE_SURVEYS)
    public String surveys;

//    String maps;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year = (Year) o;
        return id == year.id &&
                name == year.name &&
                Objects.equals(objectId, year.objectId) &&
                Objects.equals(welcome, year.welcome) &&
                Objects.equals(info, year.info) &&
                Objects.equals(surveys, year.surveys);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, objectId, name, welcome, info, surveys);
    }
}

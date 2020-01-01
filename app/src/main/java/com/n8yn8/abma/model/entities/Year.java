package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year = (Year) o;
        return Objects.equals(id, year.id) &&
                Objects.equals(objectId, year.objectId) &&
                Objects.equals(name, year.name) &&
                Objects.equals(welcome, year.welcome) &&
                Objects.equals(info, year.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectId, name, welcome, info);
    }
}

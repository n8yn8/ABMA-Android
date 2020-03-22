package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.n8yn8.abma.model.old.DatabaseHandler;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sponsor sponsor = (Sponsor) o;
        return Objects.equals(id, sponsor.id) &&
                Objects.equals(objectId, sponsor.objectId) &&
                Objects.equals(yearId, sponsor.yearId) &&
                Objects.equals(imageUrl, sponsor.imageUrl) &&
                Objects.equals(url, sponsor.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, objectId, yearId, imageUrl, url);
    }

    @NotNull
    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", objectId='" + objectId + '\'' +
                ", yearId='" + yearId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

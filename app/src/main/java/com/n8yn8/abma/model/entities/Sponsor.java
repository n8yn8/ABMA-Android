package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "sponsors",
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = "object_id",
                childColumns = "year_id"),
        indices = {@Index(value = {"object_id"}, unique = true),
                @Index(value = "year_id")})
public class Sponsor {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "object_id")
    public String objectId;

    @ColumnInfo(name = "year_id")
    public String yearId;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "url")
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

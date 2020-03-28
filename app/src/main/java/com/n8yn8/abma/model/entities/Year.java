package com.n8yn8.abma.model.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "years",
        indices = {@Index(value = {"object_id"}, unique = true)})
public class Year {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "object_id")
    public String objectId;

    @ColumnInfo(name = "name")
    public Integer name;

    @ColumnInfo(name = "welcome")
    public String welcome;

    @ColumnInfo(name = "info")
    public String info;

    @Override
    @NonNull
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

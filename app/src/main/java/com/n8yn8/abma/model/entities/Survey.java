package com.n8yn8.abma.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "surveys",
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = "object_id",
                childColumns = "year_id"),
        indices = {@Index(value = {"url"}, unique = true),
                @Index(value = "year_id")})
public class Survey {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "year_id")
    public String yearId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "details")
    public String details;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "survey_start")
    public Long startDate;

    @ColumnInfo(name = "survey_end")
    public Long endDate;
}

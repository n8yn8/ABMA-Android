package com.n8yn8.abma.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "maps",
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = "object_id",
                childColumns = "year_id"),
        indices = {@Index(value = {"url"}, unique = true),
                @Index(value = "year_id")})
public class Map implements Parcelable {

    @Ignore
    public static final Creator<Map> CREATOR = new Creator<Map>() {
        @Override
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        @Override
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };
    @PrimaryKey
    public Integer id;
    @ColumnInfo(name = "year_id")
    public String yearId;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "url")
    public String url;

    @Ignore
    public Map() {
    }

    public Map(Integer id, String yearId, String title, String url) {
        this.id = id;
        this.yearId = yearId;
        this.title = title;
        this.url = url;
    }

    @Ignore
    protected Map(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        yearId = in.readString();
        title = in.readString();
        url = in.readString();
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(yearId);
        dest.writeString(title);
        dest.writeString(url);
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }
}

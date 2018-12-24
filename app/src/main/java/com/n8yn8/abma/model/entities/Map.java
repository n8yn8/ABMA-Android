package com.n8yn8.abma.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.n8yn8.abma.model.old.DatabaseHandler;

@Entity(tableName = DatabaseHandler.TABLE_MAPS,
        foreignKeys = @ForeignKey(
                entity = Year.class,
                parentColumns = DatabaseHandler.KEY_OBJECT_ID,
                childColumns = DatabaseHandler.KEY_YEAR_ID),
        indices = {@Index(value = {DatabaseHandler.KEY_URL}, unique = true),
                @Index(value = DatabaseHandler.KEY_YEAR_ID)})
public class Map implements Parcelable {

//    private String CREATE_MAPS_TABLE = "CREATE TABLE " + TABLE_MAPS + "("
//            + KEY_ID + " INTEGER PRIMARY KEY,"
//            + KEY_YEAR_ID + " STRING,"
//            + KEY_TITLE + " STRING,"
//            + KEY_URL + " TEXT,"
//            + "UNIQUE (" + KEY_URL + ") ON CONFLICT REPLACE"
//            + ")";

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = DatabaseHandler.KEY_YEAR_ID)
    public String yearId;

    @ColumnInfo(name = DatabaseHandler.KEY_TITLE)
    public String title;

    @ColumnInfo(name = DatabaseHandler.KEY_URL)
    public String url;

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
}

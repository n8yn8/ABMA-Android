package com.n8yn8.abma.model.backendless;

import android.os.Parcel;
import android.os.Parcelable;

public class BMap implements Parcelable {

    private String title = "";
    private String url = "";

    public BMap() {

    }

    protected BMap(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    public static final Creator<BMap> CREATOR = new Creator<BMap>() {
        @Override
        public BMap createFromParcel(Parcel in) {
            return new BMap(in);
        }

        @Override
        public BMap[] newArray(int size) {
            return new BMap[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
    }
}

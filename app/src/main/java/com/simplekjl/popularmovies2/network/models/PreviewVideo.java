package com.simplekjl.popularmovies2.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PreviewVideo implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("iso_639_1")
    private String iso_639_1;
    @SerializedName("iso_3166_1")
    private String iso_3166_1;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("size")
    private int size;
    @SerializedName("type")
    private String type;

    protected PreviewVideo(Parcel in) {
        id = in.readString();
        iso_639_1 = in.readString();
        iso_3166_1 = in.readString();
        key = in.readString();
        name = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<PreviewVideo> CREATOR = new Creator<PreviewVideo>() {
        @Override
        public PreviewVideo createFromParcel(Parcel in) {
            return new PreviewVideo(in);
        }

        @Override
        public PreviewVideo[] newArray(int size) {
            return new PreviewVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso_639_1);
        dest.writeString(iso_3166_1);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeInt(size);
        dest.writeString(type);
    }
}

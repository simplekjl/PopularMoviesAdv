package com.simplekjl.popularmovies2.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideosResponse implements Parcelable {

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<PreviewVideo> results;

    protected VideosResponse(Parcel in) {
        movieId = in.readInt();
        results = in.createTypedArrayList(PreviewVideo.CREATOR);
    }

    public static final Creator<VideosResponse> CREATOR = new Creator<VideosResponse>() {
        @Override
        public VideosResponse createFromParcel(Parcel in) {
            return new VideosResponse(in);
        }

        @Override
        public VideosResponse[] newArray(int size) {
            return new VideosResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeTypedList(results);
    }
}

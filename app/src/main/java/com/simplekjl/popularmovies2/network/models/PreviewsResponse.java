package com.simplekjl.popularmovies2.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PreviewsResponse implements Parcelable {

    @SerializedName("id")
    private int movieId;
    @SerializedName("results")
    private List<PreviewVideo> results;

    protected PreviewsResponse(Parcel in) {
        movieId = in.readInt();
        results = in.createTypedArrayList(PreviewVideo.CREATOR);
    }

    public static final Creator<PreviewsResponse> CREATOR = new Creator<PreviewsResponse>() {
        @Override
        public PreviewsResponse createFromParcel(Parcel in) {
            return new PreviewsResponse(in);
        }

        @Override
        public PreviewsResponse[] newArray(int size) {
            return new PreviewsResponse[size];
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

    public int getMovieId() {
        return movieId;
    }

    public List<PreviewVideo> getResults() {
        return results;
    }
}

package com.simplekjl.popularmovies2.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse implements Parcelable {

    @SerializedName("id")
    private int id;
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Review> results;
    @SerializedName("total_pages")
    private int total_pages;
    @SerializedName("total_results")
    private int total_results;

    protected ReviewsResponse(Parcel in) {
        id = in.readInt();
        page = in.readInt();
        results = in.createTypedArrayList(Review.CREATOR);
        total_pages = in.readInt();
        total_results = in.readInt();
    }

    public static final Creator<ReviewsResponse> CREATOR = new Creator<ReviewsResponse>() {
        @Override
        public ReviewsResponse createFromParcel(Parcel in) {
            return new ReviewsResponse(in);
        }

        @Override
        public ReviewsResponse[] newArray(int size) {
            return new ReviewsResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(page);
        dest.writeTypedList(results);
        dest.writeInt(total_pages);
        dest.writeInt(total_results);
    }
}

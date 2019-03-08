/*
 * Develop by Jose L Crisostomo S. on 2/3/19 5:40 PM
 * Last modified 2/3/19 5:40 PM.
 * Copyright (c) 2019. All rights reserved.
 *
 *
 */

package com.simplekjl.popularmovies2.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse implements Parcelable {

    public static final Creator<MoviesResponse> CREATOR = new Creator<MoviesResponse>() {
        @Override
        public MoviesResponse createFromParcel(Parcel in) {
            return new MoviesResponse(in);
        }

        @Override
        public MoviesResponse[] newArray(int size) {
            return new MoviesResponse[size];
        }
    };
    @SerializedName("page")
    private String page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Movie> moviesList;

    protected MoviesResponse(Parcel in) {
        page = in.readString();
        totalResults = in.readInt();
        totalPages = in.readInt();
        moviesList = in.createTypedArrayList(Movie.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(page);
        dest.writeInt(totalResults);
        dest.writeInt(totalPages);
        dest.writeTypedList(moviesList);
    }

    public List<Movie> getMoviesList() {
        return moviesList;
    }

}

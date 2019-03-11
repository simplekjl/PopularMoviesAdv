/*
 * Develop by Jose L Crisostomo S. on 2/3/19 5:44 PM
 * Last modified 2/3/19 5:44 PM.
 * Copyright (c) 2019. All rights reserved.
 *
 *
 */

package com.simplekjl.popularmovies2.network.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    public Movie(int id, String title, String originalTitle, String overview,String posterPath, float votesAvg, float popularity, String releaseDate){
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview= overview;
        this.posterPath = posterPath;
        this.votesAvg = votesAvg;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("vote_average")
    private float votesAvg;
    @SerializedName("popularity")
    private float popularity;
    @SerializedName("release_date")
    private String releaseDate;

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        votesAvg = in.readFloat();
        popularity = in.readFloat();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeFloat(votesAvg);
        dest.writeFloat(popularity);
        dest.writeString(releaseDate);
    }


    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public float getVotesAvg() {
        return votesAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public float getPopularity() {
        return popularity;
    }
}

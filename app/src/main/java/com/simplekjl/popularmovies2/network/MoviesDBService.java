package com.simplekjl.popularmovies2.network;

import com.simplekjl.popularmovies2.network.models.MoviesResponse;
import com.simplekjl.popularmovies2.network.models.ReviewsResponse;
import com.simplekjl.popularmovies2.network.models.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesDBService {
    //https://api.themoviedb.org/3/movie/
    @GET("top_rated")
    Call<MoviesResponse> getHighestRatedMovies(@Query("api_key") String apiKey);

    @GET("popular")
    Call<MoviesResponse> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("/movie/{id}/videos")
    Call<VideosResponse> getPreviewVideosById(@Path("id") String user);

    @GET("/movie/{id}/reviews")
    Call<ReviewsResponse> getReviewsById(@Path("id") String user);
}

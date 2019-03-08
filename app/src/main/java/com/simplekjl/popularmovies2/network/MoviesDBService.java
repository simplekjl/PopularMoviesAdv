package com.simplekjl.popularmovies2.network;

import com.simplekjl.popularmovies2.network.models.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesDBService {
    //https://api.themoviedb.org/3/movie/
    @GET("top_rated")
    Call<MoviesResponse> getHihestRatedMovies(@Query("api_key") String apiKey);

    @GET("popular")
    Call<MoviesResponse> getMostPopularMovies(@Query("api_key") String apiKey);
}

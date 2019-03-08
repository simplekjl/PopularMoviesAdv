/*
 * Develop by Jose L Crisostomo S. on 2/3/19 6:00 PM
 * Last modified 2/3/19 6:00 PM.
 * Copyright (c) 2019. All rights reserved.
 *
 *
 */

package com.simplekjl.popularmovies2.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesDBClient {

    public static Retrofit sRetrofit = null;
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public MoviesDBClient(){
        //empty constructor
    }

    public static Retrofit getInstance(){
        if(sRetrofit != null){
            return sRetrofit;
        }else{
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return sRetrofit;
        }
    }

    public static <T> T createService(Class<T> serviceClass){
        return getInstance().create(serviceClass);
    }
}

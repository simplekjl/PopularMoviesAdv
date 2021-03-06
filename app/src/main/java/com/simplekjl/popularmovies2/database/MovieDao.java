package com.simplekjl.popularmovies2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import com.simplekjl.popularmovies2.network.models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY popularity")
    LiveData<List<Movie>> loadSavedMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Query("SELECT * FROM movie WHERE id == :movieId")
    Movie getMovieById(int movieId);

}

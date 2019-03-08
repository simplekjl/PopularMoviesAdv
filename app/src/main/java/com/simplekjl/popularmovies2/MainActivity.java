package com.simplekjl.popularmovies2;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplekjl.popularmovies2.adapters.MoviesAdapter;
import com.simplekjl.popularmovies2.network.MoviesDBClient;
import com.simplekjl.popularmovies2.network.MoviesDBService;
import com.simplekjl.popularmovies2.network.models.Movie;
import com.simplekjl.popularmovies2.network.models.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static String MOVIE_OBJECT = "movie";
    private static String TAG = MainActivity.class.getCanonicalName();
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageTv;
    private List<Movie> mMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        if (mMovieList != null) {
            mMoviesAdapter = new MoviesAdapter(mMovieList);
            showResults();
        } else {
            getMostPopularMovies();
        }
    }

    private void bindViews() {
        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.rv_movies);
        mErrorMessageTv = findViewById(R.id.error_message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_most_popular:
                getMostPopularMovies();
                return true;
            case R.id.action_highest_rated:
                getTopRatedMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getTopRatedMovies() {
        MoviesDBService service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        Call<MoviesResponse> result = service.getHihestRatedMovies(getString(R.string.api_key));
        showloader();
        result.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MoviesResponse moviesResponse = response.body();
                        if (moviesResponse.getMoviesList() != null) {
                            mMovieList = moviesResponse.getMoviesList();
                            mMoviesAdapter = new MoviesAdapter(mMovieList);
                            showResults();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void getMostPopularMovies() {
        MoviesDBService service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        Call<MoviesResponse> result = service.getMostPopularMovies(getString(R.string.api_key));
        showloader();
        result.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MoviesResponse moviesResponse = response.body();
                        if (moviesResponse.getMoviesList() != null) {
                            mMoviesAdapter = new MoviesAdapter(moviesResponse.getMoviesList());
                            showResults();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(MainActivity.MOVIE_OBJECT);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMovieList != null) {
            outState.putParcelableArrayList(MainActivity.MOVIE_OBJECT, (ArrayList<? extends Parcelable>) mMovieList);
        }
        super.onSaveInstanceState(outState);

    }


    void showloader() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTv.setVisibility(View.INVISIBLE);
    }

    //region ShowResults
    void showResults() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTv.setVisibility(View.INVISIBLE);
        //setup recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

    }
    //endRegion showresults

    //region Error Message
    void showErrorMessage() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTv.setVisibility(View.VISIBLE);
    }
    //endRegion error message
}

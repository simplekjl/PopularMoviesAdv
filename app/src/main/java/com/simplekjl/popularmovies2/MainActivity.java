package com.simplekjl.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.simplekjl.popularmovies2.adapters.MoviesAdapter;
import com.simplekjl.popularmovies2.database.AppDatabase;
import com.simplekjl.popularmovies2.databinding.ActivityMainBinding;
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
    private MoviesAdapter mMoviesAdapter;
    private List<Movie> mMovieList;

    private ActivityMainBinding mBinding;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDb = AppDatabase.getInstance(getApplicationContext());
        if (mMovieList != null) {
            mMoviesAdapter = new MoviesAdapter(mMovieList);
            showResults();
        } else if (isOnline()) {
            getMostPopularMovies();
        } else {
            getMoviesFromDatabase();
        }
    }


    private void getMoviesFromDatabase() {
        final LiveData<List<Movie>> data = mDb.movieDao().loadSavedMovies();
        data.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null) {
                    mMoviesAdapter = new MoviesAdapter(movies);
                    showResults();
                } else {
                    showErrorMessage();
                    Log.d(TAG, "Database is empty");
                }
            }
        });

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
            case R.id.favorites:
                getMoviesFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getTopRatedMovies() {
        MoviesDBService service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        Call<MoviesResponse> result = service.getHighestRatedMovies(BuildConfig.ApiKey);
        showLoader();
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
                Log.e(TAG, "TopRatedMovies Failed: " + t.getMessage());
            }
        });
    }

    private void getMostPopularMovies() {
        MoviesDBService service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        Call<MoviesResponse> result = service.getMostPopularMovies(BuildConfig.ApiKey);
        showLoader();
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
                Log.e(TAG, "Popular movies Failed: " + t.getMessage());
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


    void showLoader() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.rvMovies.setVisibility(View.INVISIBLE);
        mBinding.errorMessage.setVisibility(View.INVISIBLE);
    }

    //region ShowResults
    void showResults() {
        mBinding.progressBar.setVisibility(View.INVISIBLE);
        mBinding.rvMovies.setVisibility(View.VISIBLE);
        mBinding.errorMessage.setVisibility(View.INVISIBLE);
        //setup recyclerView
        int columns = numberOfColums();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        mBinding.rvMovies.setLayoutManager(gridLayoutManager);
        mBinding.rvMovies.setAdapter(mMoviesAdapter);

    }
    //endRegion showresults

    int numberOfColums() {
        if (getResources().getConfiguration().isLayoutSizeAtLeast(1)
                || getResources().getConfiguration().isLayoutSizeAtLeast(2)) {
            return 1;
        } else if (getResources().getConfiguration().isLayoutSizeAtLeast(3) ||
                getResources().getConfiguration().isLayoutSizeAtLeast(4)) {
            return 2;
        } else {
            //default value
            return 1;
        }
    }

    //region Error Message
    void showErrorMessage() {
        mBinding.progressBar.setVisibility(View.INVISIBLE);
        mBinding.rvMovies.setVisibility(View.INVISIBLE);
        mBinding.errorMessage.setVisibility(View.VISIBLE);
    }
    //endRegion error message

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}

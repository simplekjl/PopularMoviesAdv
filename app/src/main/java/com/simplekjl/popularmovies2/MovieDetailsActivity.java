package com.simplekjl.popularmovies2;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.simplekjl.popularmovies2.adapters.ReviewsAdapter;
import com.simplekjl.popularmovies2.adapters.TrailersAdapter;
import com.simplekjl.popularmovies2.database.AppDatabase;
import com.simplekjl.popularmovies2.databinding.ActivityMovieDetailsBinding;
import com.simplekjl.popularmovies2.network.MoviesDBClient;
import com.simplekjl.popularmovies2.network.MoviesDBService;
import com.simplekjl.popularmovies2.network.models.Movie;
import com.simplekjl.popularmovies2.network.models.PreviewVideo;
import com.simplekjl.popularmovies2.network.models.Review;
import com.simplekjl.popularmovies2.network.models.ReviewsResponse;
import com.simplekjl.popularmovies2.utils.AppExecutors;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getName();
    ActivityMovieDetailsBinding mBinding;
    private Movie mMovie;
    private List<PreviewVideo> mTrailers;
    private List<Review> mReviews;
    //database instance
    private AppDatabase mDb;
    private boolean isChecked = false;
    private MoviesDBService service;
    //adapters
    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        mDb = AppDatabase.getInstance(this);
        if (mMovie != null) {
            setItem(mMovie);
        } else {
            if (getIntent() != null && getIntent().getParcelableExtra(MainActivity.MOVIE_OBJECT) != null) {
                mMovie = getIntent().getParcelableExtra(MainActivity.MOVIE_OBJECT);
                setItem(mMovie);
            }
        }
        if (isOnline()) {
            getReviews(mMovie.getId());
            getTrailers(mMovie.getId());
        }
    }

    public void getReviews(int movieId) {
        Call<ReviewsResponse> result = service.getReviewsById(movieId);
        showReviewsLoader();
        result.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ReviewsResponse reviewsResponse = response.body();
                        if (reviewsResponse.getReviews() != null) {
                            showReviews(reviewsResponse.getReviews());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                showReviewsErrorMessage();
                Log.d(TAG, t.getMessage());
            }
        });


    }

    public void getTrailers(int movieId) {

    }

    void setItem(final Movie movie) {
        setTitle(movie.getTitle());
        mBinding.movieInfo.tvTitle.setText(movie.getTitle());
        mBinding.movieInfo.tvReleaseDate.setText(movie.getReleaseDate());
        mBinding.movieInfo.ratingBar.setRating(movie.getVotesAvg());
        mBinding.movieInfo.tvRating.setText(String.valueOf(movie.getVotesAvg()));
        StringBuilder sb = new StringBuilder(150);
        if (movie.getPosterPath() != null) {
            sb = new StringBuilder(150);
            sb.append(MoviesDBClient.IMAGE_BASE_URL);
            sb.append(movie.getPosterPath());
        } else {
            sb.append(MoviesDBClient.IMAGE_BASE_URL);
        }

        Picasso.get()
                .load(sb.toString())
                .placeholder(R.drawable.thumbnail)
                .error(R.drawable.thumbnail)
                .into(mBinding.cover.ivPoster);

        mBinding.movieInfo.tvSynopsis.setText(movie.getOverview());

        mBinding.cover.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMovieSaved()) {
                    mBinding.cover.saveBtn.setImageDrawable(ContextCompat.getDrawable(
                            getApplicationContext(), android.R.drawable.btn_star_big_on));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(movie);
                        }
                    });

                } else {
                    mBinding.cover.saveBtn.setImageDrawable(ContextCompat.getDrawable(
                            getApplicationContext(), android.R.drawable.btn_star_big_off));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().deleteMovie(movie);
                        }
                    });

                }
            }
        });
    }

    private boolean isMovieSaved() {
        return isChecked = !isChecked;
    }

    void showReviewsLoader() {
        mBinding.reviews.progressBar.setVisibility(View.VISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.INVISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.INVISIBLE);
    }

    //region Error message
    void showReviewsErrorMessage() {
        mBinding.reviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.INVISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.VISIBLE);
    }

    void showReviews(List<Review> reviews) {
        mBinding.reviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.VISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBinding.reviews.standardRv.setLayoutManager(linearLayoutManager);
        mReviewsAdapter = new ReviewsAdapter(reviews);
        mBinding.reviews.standardRv.setAdapter(mReviewsAdapter);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(MainActivity.MOVIE_OBJECT);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMovie != null) {
            outState.putParcelable(MainActivity.MOVIE_OBJECT, mMovie);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (service == null) {
            service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        }
    }
}

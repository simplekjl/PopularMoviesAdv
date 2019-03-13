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

import com.simplekjl.popularmovies2.adapters.ReviewsAdapter;
import com.simplekjl.popularmovies2.adapters.TrailersAdapter;
import com.simplekjl.popularmovies2.database.AppDatabase;
import com.simplekjl.popularmovies2.databinding.ActivityMovieDetailsBinding;
import com.simplekjl.popularmovies2.network.MoviesDBClient;
import com.simplekjl.popularmovies2.network.MoviesDBService;
import com.simplekjl.popularmovies2.network.models.*;
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
    ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        mDb = AppDatabase.getInstance(this);
        if (service == null) {
            service = MoviesDBClient.getInstance().create(MoviesDBService.class);
        }
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
        Call<ReviewsResponse> result = service.getReviewsById(movieId,getString(R.string.api_key));
        showReviewsLoader();
        result.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ReviewsResponse reviewsResponse = response.body();
                        if (reviewsResponse.getReviews() != null) {
                            mReviews = reviewsResponse.getReviews();
                            showReviews(mReviews);
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
        Call<PreviewsResponse> result = service.getPreviewVideosById(movieId,getString(R.string.api_key));
        showRelatedVideosLoader();
        result.enqueue(new Callback<PreviewsResponse>() {
            @Override
            public void onResponse(Call<PreviewsResponse> call, Response<PreviewsResponse> response) {
                if(response.isSuccessful()){
                    if (response.body()!=null && response.body().getResults()!= null){
                        mTrailers = response.body().getResults();
                        showRelatedVideos(mTrailers);
                    }
                }
            }

            @Override
            public void onFailure(Call<PreviewsResponse> call, Throwable t) {
                showRelatedVideosErrorMessage();
                Log.d(TAG,t.getMessage());
            }
        });

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

    void showReviewsErrorMessage() {
        mBinding.reviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.INVISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.VISIBLE);
    }

    void showReviews(List<Review> reviews) {
        mBinding.reviews.title.setText(getString(R.string.review_title));
        mBinding.reviews.title.setVisibility(View.VISIBLE);
        mBinding.reviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.VISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBinding.reviews.standardRv.setLayoutManager(linearLayoutManager);
        mReviewsAdapter = new ReviewsAdapter(reviews);
        mBinding.reviews.standardRv.setAdapter(mReviewsAdapter);
    }

    void showRelatedVideosLoader() {
        mBinding.reviews.progressBar.setVisibility(View.VISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.INVISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.INVISIBLE);
    }

    void showRelatedVideosErrorMessage() {
        mBinding.reviews.progressBar.setVisibility(View.INVISIBLE);
        mBinding.reviews.standardRv.setVisibility(View.INVISIBLE);
        mBinding.reviews.errorMessage.setVisibility(View.VISIBLE);
    }
    private void showRelatedVideos(List<PreviewVideo> mTrailers) {
        mBinding.trailerRv.title.setText(getString(R.string.trailers_title));
        mBinding.trailerRv.title.setVisibility(View.VISIBLE);
        mBinding.trailerRv.errorMessage.setVisibility(View.INVISIBLE);
        mBinding.trailerRv.progressBar.setVisibility(View.INVISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBinding.trailerRv.standardRv.setLayoutManager(linearLayoutManager);
        mTrailerAdapter = new TrailersAdapter(mTrailers);
        mBinding.trailerRv.standardRv.setAdapter(mTrailerAdapter);
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
}

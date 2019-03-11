package com.simplekjl.popularmovies2;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.simplekjl.popularmovies2.database.AppDatabase;
import com.simplekjl.popularmovies2.databinding.ActivityMovieDetailsBinding;
import com.simplekjl.popularmovies2.network.MoviesDBClient;
import com.simplekjl.popularmovies2.network.models.Movie;
import com.simplekjl.popularmovies2.utils.AppExecutors;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    ActivityMovieDetailsBinding mBinding;
    private Movie mMovie;
    //database instance
    private AppDatabase mDb;
    private boolean isChecked = false;

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
    }

    void setItem(final Movie movie) {
        setTitle(movie.getTitle());
        mBinding.tvTitle.setText(movie.getTitle());
        mBinding.tvReleaseDate.setText(movie.getReleaseDate());
        mBinding.ratingBar.setRating(movie.getVotesAvg());
        mBinding.tvRating.setText(String.valueOf(movie.getVotesAvg()));
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
                .into(mBinding.ivPoster);

        mBinding.tvSynopsis.setText(movie.getOverview());

        mBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMovieSaved()) {
                    mBinding.saveBtn.setImageDrawable(ContextCompat.getDrawable(
                            getApplicationContext(), android.R.drawable.btn_star_big_on));
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(movie);
                        }
                    });

                } else {
                    mBinding.saveBtn.setImageDrawable(ContextCompat.getDrawable(
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

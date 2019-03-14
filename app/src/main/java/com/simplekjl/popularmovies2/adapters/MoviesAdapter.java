/*
 * Develop by Jose L Crisostomo S. on 2/3/19 6:53 PM
 * Last modified 2/3/19 6:53 PM.
 * Copyright (c) 2019. All rights reserved.
 *
 *
 */

package com.simplekjl.popularmovies2.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.simplekjl.popularmovies2.MainActivity;
import com.simplekjl.popularmovies2.MovieDetailsActivity;
import com.simplekjl.popularmovies2.R;
import com.simplekjl.popularmovies2.databinding.MovieItemBinding;
import com.simplekjl.popularmovies2.network.MoviesDBClient;
import com.simplekjl.popularmovies2.network.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> mMoviesDataSet;
    private Context mContext;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public MoviesAdapter(List<Movie> data) {
        mMoviesDataSet = data;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater =  LayoutInflater.from(mContext);
        MovieItemBinding mBinding = MovieItemBinding.inflate(inflater,viewGroup,false);

        MoviesAdapterViewHolder viewHolder = new MoviesAdapterViewHolder(mBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder moviesAdapterViewHolder, int i) {

        moviesAdapterViewHolder.setItem(mMoviesDataSet.get(i));
        // Here you apply the animation when the view is bound
        setAnimation(moviesAdapterViewHolder.itemView, i);
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mMoviesDataSet.size();
    }


    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        MovieItemBinding itemBinding;
        MoviesAdapterViewHolder(@NonNull MovieItemBinding bindView) {
            super(bindView.getRoot());
            itemBinding = bindView;
        }

        void setItem(final Movie movie) {
            itemBinding.cover.saveBtn.setVisibility(View.INVISIBLE);
            itemBinding.movieInfo.tvTitle.setText(movie.getTitle());
            itemBinding.movieInfo.tvReleaseDate.setText(movie.getReleaseDate());
            itemBinding.movieInfo.ratingBar.setRating(movie.getVotesAvg());
            itemBinding.movieInfo.tvRating.setText(String.valueOf(movie.getVotesAvg()));
            itemBinding.movieInfo.tvSynopsis.setText(movie.getOverview());
            itemBinding.movieInfo.tvSynopsis.setMaxLines(4);
            itemBinding.movieInfo.tvSynopsis.setEllipsize(TextUtils.TruncateAt.END);
            StringBuilder sb = new StringBuilder(150);
            if (movie.getPosterPath() != null) {
                sb = new StringBuilder(150);
                sb.append(MoviesDBClient.IMAGE_BASE_URL);
                sb.append(movie.getPosterPath());
            } else {
                sb.append(MoviesDBClient.IMAGE_BASE_URL);
            }

            itemBinding.showMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                    intent.putExtra(MainActivity.MOVIE_OBJECT, movie);
                    mContext.startActivity(intent);
                }
            });

            Picasso.get()
                    .load(sb.toString())
                    .placeholder(R.drawable.thumbnail)
                    .error(R.drawable.thumbnail)
                    .into(itemBinding.cover.ivPoster);

        }
    }
}

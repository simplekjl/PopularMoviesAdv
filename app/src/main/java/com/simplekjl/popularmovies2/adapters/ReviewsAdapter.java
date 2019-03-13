package com.simplekjl.popularmovies2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.simplekjl.popularmovies2.databinding.ReviewItemLayoutBinding;
import com.simplekjl.popularmovies2.network.models.Review;


import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mDataSet;

    public ReviewsAdapter(List<Review> reviews) {
        mDataSet = reviews;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ReviewItemLayoutBinding mBinding = ReviewItemLayoutBinding.inflate(layoutInflater,viewGroup,false);
        ReviewsAdapterViewHolder viewHolder = new ReviewsAdapterViewHolder(mBinding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterViewHolder reviewsAdapterViewHolder, int i) {
        reviewsAdapterViewHolder.setItem(mDataSet.get(i));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{
        private ReviewItemLayoutBinding reviewItemLayoutBinding;

        public ReviewsAdapterViewHolder(@NonNull ReviewItemLayoutBinding bindView) {
            super(bindView.getRoot());
            reviewItemLayoutBinding = bindView;
        }

        public void setItem(Review review){
            reviewItemLayoutBinding.content.setText(review.getContent());
            reviewItemLayoutBinding.reviewAuthor.setText(review.getAuthor());
        }
    }
}

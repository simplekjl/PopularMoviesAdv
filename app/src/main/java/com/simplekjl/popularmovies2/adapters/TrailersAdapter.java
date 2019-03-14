package com.simplekjl.popularmovies2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplekjl.popularmovies2.databinding.PreviewItemLayoutBinding;
import com.simplekjl.popularmovies2.network.models.PreviewVideo;
import com.simplekjl.popularmovies2.utils.OnItemClickListener;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private List<PreviewVideo> mList;
    private OnItemClickListener mClickListener;

    public TrailersAdapter(List<PreviewVideo> mList, OnItemClickListener listener) {
        this.mList = mList;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        //We get rid of the use of the layout line with the bindings
        //int itemLayout = R.layout.preview_item_layout;
        boolean hasToBeAttachedRightAway = false;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        PreviewItemLayoutBinding binding = PreviewItemLayoutBinding.inflate(layoutInflater, viewGroup, hasToBeAttachedRightAway);
        return new TrailersAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapterViewHolder trailersAdapterViewHolder, int i) {
        trailersAdapterViewHolder.setupItem(mList.get(i),mClickListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

     class TrailersAdapterViewHolder extends RecyclerView.ViewHolder {
        private final PreviewItemLayoutBinding mBinding;

        TrailersAdapterViewHolder(@NonNull PreviewItemLayoutBinding previewItemBinding) {
            super(previewItemBinding.getRoot());
            mBinding = previewItemBinding;
        }

         void setupItem(final PreviewVideo previewVideo, final OnItemClickListener mClickListener) {
            mBinding.trailerTitle.setText(previewVideo.getName());
            mBinding.language.setText(previewVideo.getIso_639_1());
            mBinding.quality.setText(String.valueOf(previewVideo.getSize()));
            mBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(previewVideo);
                }
            });
        }
    }
}

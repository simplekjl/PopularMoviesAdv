package com.simplekjl.popularmovies2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.simplekjl.popularmovies2.databinding.PreviewItemLayoutBinding;
import com.simplekjl.popularmovies2.network.models.PreviewVideo;

import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private List<PreviewVideo> mList;
    private Context context;

    public TrailersAdapter(List<PreviewVideo> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        //We get rid of the use of the layout line with the bindings
        //int itemLayout = R.layout.preview_item_layout;
        boolean hasToBeAttachedRightAway = false;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        PreviewItemLayoutBinding binding = PreviewItemLayoutBinding.inflate(layoutInflater, viewGroup, hasToBeAttachedRightAway);

        TrailersAdapterViewHolder viewholder = new TrailersAdapterViewHolder(binding);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapterViewHolder trailersAdapterViewHolder, int i) {
        trailersAdapterViewHolder.setupItem(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder {
        private final PreviewItemLayoutBinding mBinding;

        public TrailersAdapterViewHolder(@NonNull PreviewItemLayoutBinding previewItemBinding) {
            super(previewItemBinding.getRoot());
            mBinding = previewItemBinding;
        }

        public void setupItem(PreviewVideo previewVideo) {
            mBinding.trailerTitle.setText(previewVideo.getName());
            mBinding.language.setText(previewVideo.getIso_639_1());
            mBinding.quality.setText(String.valueOf(previewVideo.getSize()));
        }
    }
}

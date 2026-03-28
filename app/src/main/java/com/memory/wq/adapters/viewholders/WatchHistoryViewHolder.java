package com.memory.wq.adapters.viewholders;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.beans.WatchHistoryInfo;
import com.memory.wq.databinding.ItemWatchHistoryBinding;
import com.memory.wq.interfaces.OnMovieClickListener;
import com.memory.wq.utils.TimeUtils;

public class WatchHistoryViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "WQ_WatchHistoryViewHolder";
    private ItemWatchHistoryBinding mBinding;
    private OnMovieClickListener mListener;

    public WatchHistoryViewHolder(ItemWatchHistoryBinding binding, OnMovieClickListener mListener) {
        super(binding.getRoot());
        mBinding = binding;
        this.mListener = mListener;
    }

    public void bind(WatchHistoryInfo watchHistoryInfo) {
        Glide.with(itemView.getContext())
                .load(watchHistoryInfo.getMovieInfo().getCoverUrl())
                .transform(new RoundedCorners(15))
                .into(mBinding.ivCover);
        mBinding.tvMovieTitle.setText(watchHistoryInfo.getMovieInfo().getTitle());
        mBinding.tvWatchCount.setText("已观看 " + watchHistoryInfo.getWatchCount() + " 次");
        mBinding.tvProgress.setText("观看到 "+TimeUtils.formatSecond(watchHistoryInfo.getProgress()));
//        mask
        mBinding.ivPlay.setOnClickListener(v->{
            if (mListener == null){
                Log.d(TAG, "[X] bind #37");
                return;
            }
            mListener.onCoverClick(watchHistoryInfo.getMovieInfo());
        });
    }
}

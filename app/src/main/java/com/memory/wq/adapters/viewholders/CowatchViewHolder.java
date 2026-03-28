package com.memory.wq.adapters.viewholders;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.activities.AudioActivity;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ItemCowatchLayoutBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.OnRoomClickListener;

public class CowatchViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "WQ_CowatchViewHolder";

    private final ItemCowatchLayoutBinding mBinding;
    private final OnRoomClickListener mListener;
    public CowatchViewHolder(@NonNull ItemCowatchLayoutBinding binding, OnRoomClickListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mListener = listener;
    }

    public void bind(RoomInfo roomInfo) {
        mBinding.tvMovieTitle.setText(roomInfo.getMovieName());
        Glide.with(itemView.getContext())
                .load(roomInfo.getMovieCover())
                .transform(new RoundedCorners(15))
                .into(mBinding.ivCover);


        mBinding.getRoot().setOnClickListener(v -> {
            if (mListener==null){
                Log.d(TAG, "[x] bind #40");
                return;
            }

            mListener.onRoomClick(roomInfo);
        });
    }
}

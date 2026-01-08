package com.memory.wq.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemFriendBinding;
import com.memory.wq.interfaces.OnMemberClickListener;

public class FriendViewHolder extends RecyclerView.ViewHolder {

    private final ItemFriendBinding binding;
    private final OnMemberClickListener mListener;

    public FriendViewHolder(@NonNull ItemFriendBinding binding, OnMemberClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.mListener = listener;
    }

    public void bind(FriendInfo friendInfo) {
        binding.tvFriendNickname.setText(friendInfo.getNickname());
        binding.ivOnlineState.setVisibility(friendInfo.isOnline() ? View.VISIBLE : View.GONE);
        Glide.with(binding.getRoot().getContext())
                .load(friendInfo.getAvatarUrl())
                .into(binding.ivFriendAvatar);

        binding.getRoot().setOnClickListener(v -> {
            mListener.onMemberClick(friendInfo.getUuNumber());
        });
    }
}
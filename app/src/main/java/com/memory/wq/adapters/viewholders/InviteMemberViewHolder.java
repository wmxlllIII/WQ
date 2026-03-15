package com.memory.wq.adapters.viewholders;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemInviteMemberBinding;
import com.memory.wq.interfaces.OnInviteClickListener;
import com.memory.wq.interfaces.OnMemberClickListener;

public class InviteMemberViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "WQ_IMVH";
    private final ItemInviteMemberBinding binding;
    private final OnInviteClickListener listener;

    public InviteMemberViewHolder(ItemInviteMemberBinding binding, OnInviteClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
    }

    public void bind(FriendInfo friend, boolean checked) {
        binding.tvNickname.setText(friend.getNickname());
        Glide.with(binding.getRoot().getContext())
                .load(friend.getAvatarUrl())
                .into(binding.ivAvatar);

        binding.cbSelect.setChecked(checked);
        binding.getRoot().setOnClickListener(v -> {
            if (listener == null) {
                Log.d(TAG, "[X] bind #35");
                return;
            }

            listener.onCheckedChanged(friend.getUuNumber());
        });
    }
}

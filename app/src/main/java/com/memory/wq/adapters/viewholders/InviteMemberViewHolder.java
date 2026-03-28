package com.memory.wq.adapters.viewholders;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.adapters.InviteMemberAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemInviteMemberBinding;
import com.memory.wq.interfaces.OnInviteClickListener;
import com.memory.wq.interfaces.OnMemberClickListener;

public class InviteMemberViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "WQ_IMVH";
    private final ItemInviteMemberBinding binding;
    private final OnInviteClickListener listener;
    private  InviteMemberAdapter adapter;

    public InviteMemberViewHolder(ItemInviteMemberBinding binding, OnInviteClickListener listener,  InviteMemberAdapter adapter) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
        this.adapter = adapter;
    }

    public void bind(FriendInfo friend, boolean checked) {
        binding.tvNickname.setText(friend.getNickname());
        Glide.with(binding.getRoot().getContext())
                .load(friend.getAvatarUrl())
                .error(R.mipmap.icon_default_avatar)
                .transform(new RoundedCorners(12))
                .into(binding.ivAvatar);

        binding.cbSelect.setOnCheckedChangeListener(null);
        binding.cbSelect.setChecked(checked);
        binding.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            adapter.toggleUser(friend.getUuNumber(), position);
        });

        binding.getRoot().setOnClickListener(v -> {
            if (listener == null) {
                Log.d(TAG, "[X] bind #35");
                return;
            }

            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            adapter.toggleUser(friend.getUuNumber(), position);

        });

        binding.cbSelect.setChecked(checked);
        binding.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckedChanged(friend.getUuNumber());
            }
        });
    }
}

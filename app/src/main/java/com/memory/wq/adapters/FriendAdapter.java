package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ItemFriendBinding;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.adapters.diffcallbacks.FriDiffCallback;

public class FriendAdapter extends ListAdapter<FriendInfo, RecyclerView.ViewHolder> {

    private final static String TAG = "WQ_FriendAdapter";
    private final OnFriItemClickListener mListener;

    public FriendAdapter(OnFriItemClickListener listener) {
        super(new FriDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendInfo friendInfo = getItem(position);

//        holder.iv_friend_avatar.setImageUrl(friendInfo.getAvatarUrl());
        FriendViewHolder friendVH = (FriendViewHolder) holder;
        Glide.with(holder.itemView.getContext())
                .load(friendInfo.getAvatarUrl())
                .circleCrop()
                .into(friendVH.iv_friend_avatar);
        friendVH.tv_friend_nickname.setText(friendInfo.getNickname());
        friendVH.iv_online_state.setVisibility(friendInfo.isOnline() ? View.VISIBLE : View.GONE);

        friendVH.iv_friend_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(friendInfo.getUuNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_friend_avatar;
        ImageView iv_online_state;
        TextView tv_friend_nickname;


        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_friend_avatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            iv_online_state = (ImageView) itemView.findViewById(R.id.iv_online_state);
            tv_friend_nickname = (TextView) itemView.findViewById(R.id.tv_friend_nickname);
        }
    }
}

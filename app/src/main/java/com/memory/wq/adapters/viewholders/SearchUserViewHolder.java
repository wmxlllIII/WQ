package com.memory.wq.adapters.viewholders;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemSearchFriendBinding;
import com.memory.wq.interfaces.OnFriItemClickListener;

public class SearchUserViewHolder extends RecyclerView.ViewHolder {

    private final ItemSearchFriendBinding mBinding;

    public SearchUserViewHolder(ItemSearchFriendBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(FriendInfo friend, OnFriItemClickListener listener) {
        mBinding.tvNickName.setText(friend.getNickname());

        Glide.with(mBinding.getRoot().getContext())
                .load(friend.getAvatarUrl())
                .error(R.mipmap.icon_default_avatar)
                .transform(new RoundedCorners(12))
                .into(mBinding.ivAvatar);

        mBinding.getRoot().setOnClickListener(view -> listener.onItemClick(friend.getUuNumber()));
    }
}

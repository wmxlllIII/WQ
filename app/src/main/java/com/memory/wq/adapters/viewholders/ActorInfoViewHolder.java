package com.memory.wq.adapters.viewholders;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.ActorInfo;
import com.memory.wq.databinding.ItemActorBinding;
import com.memory.wq.interfaces.OnActorClickListener;

public class ActorInfoViewHolder extends RecyclerView.ViewHolder {
    private final ItemActorBinding binding;

    public ActorInfoViewHolder(ItemActorBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ActorInfo actorInfo, OnActorClickListener mListener) {
        binding.tvActorName.setText(actorInfo.getActorName());
        Glide.with(binding.getRoot().getContext())
                .load(actorInfo.getAvatarUrl())
                .error(R.mipmap.icon_default_avatar)
                .circleCrop()
                .into(binding.ivActorAvatar);
        binding.getRoot().setOnClickListener(v ->
                mListener.onActorClick(actorInfo.getActorId())
        );
    }
}

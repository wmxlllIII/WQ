package com.memory.wq.adapters.diffcallbacks;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.memory.wq.beans.ActorInfo;

public class ActorDiffCallback extends DiffUtil.ItemCallback<ActorInfo>{
    @Override
    public boolean areItemsTheSame(@NonNull ActorInfo oldItem, @NonNull ActorInfo newItem) {
        return oldItem.getActorId() == newItem.getActorId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull ActorInfo oldItem, @NonNull ActorInfo newItem) {
        return oldItem.getActorId() == newItem.getActorId() ;
    }
}

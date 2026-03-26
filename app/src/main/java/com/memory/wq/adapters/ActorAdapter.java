package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.ActorDiffCallback;
import com.memory.wq.adapters.diffcallbacks.MovieProfileDiffCallback;
import com.memory.wq.adapters.viewholders.ActorInfoViewHolder;
import com.memory.wq.adapters.viewholders.MovieProfileViewHolder;
import com.memory.wq.beans.ActorInfo;
import com.memory.wq.beans.MovieProfileInfo;
import com.memory.wq.databinding.ItemActorBinding;
import com.memory.wq.interfaces.OnActorClickListener;

public class ActorAdapter extends ListAdapter<ActorInfo, ActorInfoViewHolder> {

    private final OnActorClickListener mListener;

    public ActorAdapter(OnActorClickListener listener) {
        super(new ActorDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ActorInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActorBinding binding = ItemActorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ActorInfoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorInfoViewHolder holder, int position) {
        ActorInfo actorInfo = getItem(position);
        holder.bind(actorInfo, mListener);
    }
}

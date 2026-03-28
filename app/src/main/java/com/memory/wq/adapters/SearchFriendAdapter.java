package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.FriDiffCallback;
import com.memory.wq.adapters.viewholders.SearchUserViewHolder;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemSearchFriendBinding;
import com.memory.wq.interfaces.OnFriItemClickListener;

public class SearchFriendAdapter extends ListAdapter<FriendInfo, SearchUserViewHolder> {

    public static final String TAG = "WQ_SearchFriendAdapter";
    private final OnFriItemClickListener mListener;

    public SearchFriendAdapter(OnFriItemClickListener listener) {
        super(new FriDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchFriendBinding binding = ItemSearchFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchUserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position) {
        FriendInfo friendInfo = getItem(position);
        holder.bind(friendInfo, mListener);
    }
}

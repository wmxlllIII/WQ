package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.memory.wq.adapters.diffcallbacks.MsgListDiffCallback;
import com.memory.wq.adapters.viewholders.MsgListViewHolder;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.MsgListInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.databinding.ItemMsgsLayoutBinding;
import com.memory.wq.interfaces.OnMsgItemClickListener;
import com.memory.wq.interfaces.OnMsgListClickListener;

public class MsgListAdapter extends ListAdapter<MsgListInfo, MsgListViewHolder> {

    private final OnMsgListClickListener mListener;
    public MsgListAdapter(OnMsgListClickListener listener) {
        super(new MsgListDiffCallback());
        mListener = listener;
    }

    @NonNull
    @Override
    public MsgListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMsgsLayoutBinding binding = ItemMsgsLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MsgListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgListViewHolder holder, int position) {
        MsgListInfo msgListInfo = getItem(position);

        holder.bind(msgListInfo, mListener);
    }
}

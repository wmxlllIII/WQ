package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.adapters.diffcallbacks.MemberDiffCallback;
import com.memory.wq.adapters.viewholders.FriendViewHolder;
import com.memory.wq.adapters.viewholders.InviteViewHolder;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemFriendBinding;
import com.memory.wq.databinding.ItemInviteBinding;
import com.memory.wq.interfaces.OnMemberClickListener;

public class MemberAdapter extends ListAdapter<FriendInfo, RecyclerView.ViewHolder> {

    private final static String TAG = "WQ_MemberAdapter";
    private final static int VIEW_TYPE_NORMAL = 0;
    private final static int VIEW_TYPE_INVITE = 1;
    private final static int VIEW_TYPE_SHOW_ALL = 2;
    private final static int MAX_DISPLAY_MEMBERS = 8;
    private final OnMemberClickListener mListener;


    public MemberAdapter(OnMemberClickListener listener) {
        super(new MemberDiffCallback());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHOW_ALL) {
            //todo 显示所有
        } else if (viewType == VIEW_TYPE_INVITE) {
            ItemInviteBinding binding = ItemInviteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new InviteViewHolder(binding, mListener);
        }

        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int listSize = getCurrentList().size();

        if (holder instanceof FriendViewHolder) {
            if (position < Math.min(listSize, MAX_DISPLAY_MEMBERS)) {
                FriendViewHolder friendVH = (FriendViewHolder) holder;
                friendVH.bind(getCurrentList().get(position));
            }
        } else if (holder instanceof InviteViewHolder) {
            InviteViewHolder inviteVH = (InviteViewHolder) holder;
            inviteVH.bind();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int listSize = getCurrentList().size();
        int displayMemberCount = Math.min(listSize, MAX_DISPLAY_MEMBERS);

        if (position < displayMemberCount) {
            return VIEW_TYPE_NORMAL;
        } else if (listSize > MAX_DISPLAY_MEMBERS && position == displayMemberCount) {
            return VIEW_TYPE_SHOW_ALL;
        } else {
            return VIEW_TYPE_INVITE;
        }
    }


    @Override
    public int getItemCount() {
        int listSize = getCurrentList().size();
        int displayMemberCount = Math.min(listSize, MAX_DISPLAY_MEMBERS);

        if (listSize == 0) {
            return 1;
        } else if (listSize > MAX_DISPLAY_MEMBERS) {
            return MAX_DISPLAY_MEMBERS + 2;
        } else {
            return listSize + 1;
        }
    }
}

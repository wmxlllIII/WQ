package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.adapters.diffcallbacks.MemberDiffCallback;
import com.memory.wq.adapters.viewholders.InviteMemberViewHolder;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.ItemInviteMemberBinding;
import com.memory.wq.interfaces.OnInviteClickListener;
import com.memory.wq.interfaces.OnMemberClickListener;

import java.util.HashSet;
import java.util.Set;

public class InviteMemberAdapter extends ListAdapter<FriendInfo, InviteMemberViewHolder> {

    private final OnInviteClickListener mListener = new OnInviteClickListenerImpl();

    private final Set<Long> selectedUsers = new HashSet<>();


    public InviteMemberAdapter() {
        super(new MemberDiffCallback());
    }

    @NonNull
    @Override
    public InviteMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInviteMemberBinding binding = ItemInviteMemberBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new InviteMemberViewHolder(binding, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull InviteMemberViewHolder holder, int position) {
        FriendInfo friend = getItem(position);

        boolean isChecked = selectedUsers.contains(friend.getUuNumber());

        holder.bind(friend, isChecked);
    }

    public Set<Long> getSelectedUsers() {
        return selectedUsers;
    }

    private class OnInviteClickListenerImpl implements OnInviteClickListener {

        @Override
        public void onCheckedChanged(long targetId) {
            boolean isChecked = selectedUsers.contains(targetId);
            if (isChecked) {
                selectedUsers.add(targetId);
            } else {
                selectedUsers.remove(targetId);
            }
        }
    }

}

package com.memory.wq.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.memory.wq.R;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.enumertions.FriendRelaStatus;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.adapters.diffcallbacks.FriRelaDiffCallback;

public class FriendRelaAdapter extends ListAdapter<FriendRelaInfo, RecyclerView.ViewHolder> {
    public static final String TAG = "WQ_FriendRelaAdapter";

    private OnFriItemClickListener listener;


    public FriendRelaAdapter(OnFriItemClickListener listener) {
        super(new FriRelaDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRelaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request_layout, parent, false);
        return new FriendRelaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FriendRelaViewHolder)) {
            return;
        }

        updateui((FriendRelaViewHolder) holder, position);
    }

    private void updateui(FriendRelaViewHolder holder, int position) {
        FriendRelaInfo friendRela = getItem(position);
        boolean isReceiver = friendRela.getReceiverId() == AccountManager.getUserInfo().getUuNumber();

        Glide.with(holder.itemView.getContext())
                .load(isReceiver ? friendRela.getSenderAvatar() : friendRela.getReceiverAvatar())
                .error(R.mipmap.icon_default_avatar)
                .transform(new RoundedCorners(12))
                .into(holder.iv_avatar);
        holder.tv_nickname.setText(isReceiver ? friendRela.getSenderName() : friendRela.getReceiverName());
        holder.tv_verify_message.setText(friendRela.getValidMsg());


        if (isReceiver) {
            if (friendRela.getStatus() == FriendRelaStatus.PENDING.toInt()) {
                holder.tv_friend_state.setVisibility(View.GONE);
                holder.iv_accept.setVisibility(View.VISIBLE);
                holder.iv_reject.setVisibility(View.VISIBLE);

            } else {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.iv_accept.setVisibility(View.GONE);
                holder.iv_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText(friendRela.getStatus() == FriendRelaStatus.ACCEPTED.toInt() ? "已添加" : "已拒绝");
            }
        } else {
            if (friendRela.getStatus() == FriendRelaStatus.PENDING.toInt()) {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.iv_accept.setVisibility(View.GONE);
                holder.iv_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText("已申请");

            } else {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.iv_accept.setVisibility(View.GONE);
                holder.iv_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText(friendRela.getStatus() == FriendRelaStatus.ACCEPTED.toInt() ? "已添加" : "待验证");
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(friendRela.getChatId()));
        holder.iv_accept.setOnClickListener(v -> listener.onUpdateClick(friendRela.getSenderId(), true, friendRela.getValidMsg()));
        holder.iv_reject.setOnClickListener(v -> listener.onUpdateClick(friendRela.getSenderId(), false, friendRela.getValidMsg()));

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class FriendRelaViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_avatar;
        TextView tv_nickname;
        TextView tv_verify_message;
        ImageView iv_accept, iv_reject;
        TextView tv_friend_state;

        private FriendRelaViewHolder(View convertView) {
            super(convertView);
            iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            tv_verify_message = (TextView) convertView.findViewById(R.id.tv_verify_message);
            iv_accept = (ImageView) convertView.findViewById(R.id.iv_accept);
            iv_reject = (ImageView) convertView.findViewById(R.id.iv_reject);
            tv_friend_state = (TextView) convertView.findViewById(R.id.tv_friend_state);
        }
    }
}
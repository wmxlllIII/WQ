package com.memory.wq.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.utils.diffutils.FriRelaDiffCallback;

import java.util.List;

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
        Log.d(TAG, "=============信息： " + friendRela);
        boolean isReceiver = friendRela.getTargetEmail().equals(AccountManager.getUserInfo(holder.itemView.getContext()).getEmail());


        Glide.with(holder.itemView.getContext())
                .load(isReceiver ? friendRela.getSourceAvatarUrl() : friendRela.getTargetAvatarUrl())
                .into(holder.iv_avatar);
        holder.tv_nickname.setText(isReceiver ? friendRela.getSourceNickname() : friendRela.getTargetNickname());
        holder.tv_verify_message.setText(friendRela.getValidMsg());


        if (isReceiver) {
            if ("sended".equals(friendRela.getState())) {
                holder.tv_friend_state.setVisibility(View.GONE);
                holder.btn_accept.setVisibility(View.VISIBLE);
                holder.btn_reject.setVisibility(View.VISIBLE);

            } else {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText(friendRela.getState().equals("accepted") ? "已添加" : "已拒绝");
            }
        } else {
            if ("sended".equals(friendRela.getState())) {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText("已申请");

            } else {
                holder.tv_friend_state.setVisibility(View.VISIBLE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.tv_friend_state.setText("accepted".equals(friendRela.getState()) ? "已添加" : "待验证");
            }
        }

        holder.btn_accept.setOnClickListener(v -> listener.onAcceptClick(friendRela.getSourceEmail()));
        holder.btn_reject.setOnClickListener(v -> listener.onRejectClick(friendRela.getSourceEmail()));


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class FriendRelaViewHolder extends RecyclerView.ViewHolder {
        //TODO IMAGEVIEW
        ImageView iv_avatar;
        TextView tv_nickname;
        TextView tv_verify_message;
        ImageButton btn_accept;
        ImageButton btn_reject;
        TextView tv_friend_state;

        private FriendRelaViewHolder(View convertView) {
            super(convertView);
            iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            tv_verify_message = (TextView) convertView.findViewById(R.id.tv_verify_message);
            btn_accept = (ImageButton) convertView.findViewById(R.id.btn_accept);
            btn_reject = (ImageButton) convertView.findViewById(R.id.btn_reject);
            tv_friend_state = (TextView) convertView.findViewById(R.id.tv_friend_state);
        }
    }
}
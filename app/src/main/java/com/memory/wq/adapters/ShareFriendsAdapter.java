package com.memory.wq.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.activities.MsgActivity;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.properties.AppProperties;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShareFriendsAdapter extends RecyclerView.Adapter<ShareFriendsAdapter.ViewHolder> {

    private Context context;
    private List<FriendInfo> friendInfoList;
    private OnFriendClickListener listener;

    public ShareFriendsAdapter(Context context, List<FriendInfo> friendInfoList) {
        this.context = context;
        this.friendInfoList = friendInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_friend, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendInfo friendInfo = friendInfoList.get(position);

//        holder.iv_friend_avatar.setImageUrl(friendInfo.getAvatarUrl());

        Glide.with(context)
                .load(AppProperties.HTTP_SERVER_ADDRESS + friendInfo.getAvatarUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.load_failure)
                .circleCrop()
                .into(holder.iv_friend_avatar);
        holder.tv_friend_nickname.setText(friendInfo.getNickname());
        holder.iv_online_state.setVisibility(friendInfo.isOnline() ? View.VISIBLE : View.GONE);
        holder.iv_friend_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onFriendClick(friendInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_friend_avatar;
        CircleImageView iv_online_state;
        TextView tv_friend_nickname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_friend_avatar = (ImageView) itemView.findViewById(R.id.iv_friend_avatar);
            iv_online_state = (CircleImageView) itemView.findViewById(R.id.iv_online_state);
            tv_friend_nickname = (TextView) itemView.findViewById(R.id.tv_friend_nickname);
        }
    }

    public interface OnFriendClickListener {
        void onFriendClick(FriendInfo friendInfo);
    }

    public void setOnFriendClickListener(OnFriendClickListener listener) {
        this.listener = listener;
    }
}

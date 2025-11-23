package com.memory.wq.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.interfaces.OnMsgItemClickListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.utils.diffutils.MsgInfoDiffCallback;

import java.util.List;


public class MsgAdapter extends ListAdapter<MsgInfo, RecyclerView.ViewHolder> {
    private List<MsgInfo> msgInfoList;
    private OnMsgItemClickListener listener;

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_LINK = 1;

    public MsgAdapter(OnMsgItemClickListener listener) {
        super(new MsgInfoDiffCallback());
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        return msgInfoList.get(position).getMsgType() == 0 ? TYPE_TEXT : TYPE_LINK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LINK) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_link_layout, parent, false);
            return new LinkViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_layout, parent, false);
            return new TextViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgInfo msgInfo = msgInfoList.get(position);

        if (TextUtils.isEmpty(AccountManager.getUserInfo(holder.itemView.getContext()).getEmail()) || msgInfo == null) {
            if (holder instanceof TextViewHolder) {
                TextViewHolder textHolder = (TextViewHolder) holder;
                textHolder.ll_left.setVisibility(View.GONE);
                textHolder.ll_right.setVisibility(View.GONE);
            } else if (holder instanceof LinkViewHolder) {
                LinkViewHolder linkHolder = (LinkViewHolder) holder;
                linkHolder.ll_left_link.setVisibility(View.GONE);
                linkHolder.ll_right_link.setVisibility(View.GONE);
            }
            return;
        }

        if (holder instanceof TextViewHolder) {
            handleTextMessage((TextViewHolder) holder, msgInfo);

        } else if (holder instanceof LinkViewHolder) {
            handleLinkMessage((LinkViewHolder) holder, msgInfo);
        }

    }

    private void handleLinkMessage(LinkViewHolder holder, MsgInfo msgInfo) {
        String linkImageUrl = msgInfo.getLinkImageUrl();
        String linkTitle = msgInfo.getLinkTitle();
        //房间id
        String linkContent = msgInfo.getLinkContent();
        String content = msgInfo.getContent();

        if (msgInfo.getSenderEmail().equals(
                AccountManager.getUserInfo(holder.itemView.getContext()).getEmail()
        )
        ) {
            holder.ll_left_link.setVisibility(View.GONE);
            holder.ll_right_link.setVisibility(View.VISIBLE);


            Glide.with(holder.itemView.getContext())
                    .load(linkImageUrl)
                    .into(holder.iv_link_image_right);
            holder.tv_link_title_right.setText(linkTitle);
            holder.tv_link_content_right.setText(content);

            Glide.with(holder.itemView.getContext())
                    .load(msgInfo.getMyAvatarUrl())
                    .into(holder.iv_my_avatar_link);
        } else {

            holder.ll_left_link.setVisibility(View.VISIBLE);
            holder.ll_right_link.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext())
                    .load(linkImageUrl)
                    .into(holder.iv_link_image_left);
            holder.tv_link_title_left.setText(linkTitle);
            holder.tv_link_content_left.setText(content);

            Glide.with(holder.itemView.getContext())
                    .load(msgInfo.getFriendAvatarUrl())
                    .into(holder.iv_friend_avatar_link);
        }

        holder.ll_left_link.setOnClickListener(v -> listener.onLinkClick(msgInfo));
        holder.ll_right_link.setOnClickListener(v -> listener.onLinkClick(msgInfo));
    }

    private void handleTextMessage(TextViewHolder holder, MsgInfo msgInfo) {
        if (msgInfo.getSenderEmail().equals(
                AccountManager.getUserInfo(holder.itemView.getContext()).getEmail()
        )
        ) {
            holder.ll_left.setVisibility(View.GONE);
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.tv_rightMsg.setText(msgInfo.getContent());
        } else {
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.ll_right.setVisibility(View.GONE);
            holder.tv_leftMsg.setText(msgInfo.getContent());
        }

        Glide.with(holder.iv_friend_avatar.getContext())
                .load(msgInfo.getFriendAvatarUrl())
                .into(holder.iv_friend_avatar);

        Glide.with(holder.iv_my_avatar.getContext())
                .load(msgInfo.getMyAvatarUrl())
                .into(holder.iv_my_avatar);
    }


    @Override
    public int getItemCount() {
        return msgInfoList.size();
    }


    static class TextViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_left, ll_right;
        TextView tv_leftMsg, tv_rightMsg;
        ImageView iv_friend_avatar, iv_my_avatar;

        public TextViewHolder(@NonNull View view) {
            super(view);
            ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
            ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
            tv_leftMsg = (TextView) view.findViewById(R.id.tv_leftMsg);
            tv_rightMsg = (TextView) view.findViewById(R.id.tv_rightMsg);
            iv_friend_avatar = (ImageView) view.findViewById(R.id.iv_friend_avatar);
            iv_my_avatar = (ImageView) view.findViewById(R.id.iv_my_avatar);
        }

    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_left_link;
        LinearLayout ll_right_link;
        ImageView iv_friend_avatar_link;
        ImageView iv_my_avatar_link;
        ImageView iv_link_image_left;
        ImageView iv_link_image_right;
        TextView tv_link_title_left;
        TextView tv_link_title_right;
        TextView tv_link_content_left;
        TextView tv_link_content_right;

        public LinkViewHolder(@NonNull View view) {
            super(view);
            ll_left_link = (LinearLayout) view.findViewById(R.id.ll_left_link);
            ll_right_link = (LinearLayout) view.findViewById(R.id.ll_right_link);
            iv_friend_avatar_link = (ImageView) view.findViewById(R.id.iv_friend_avatar_link);
            iv_my_avatar_link = (ImageView) view.findViewById(R.id.iv_my_avatar_link);
            iv_link_image_left = (ImageView) view.findViewById(R.id.iv_link_image_left);
            iv_link_image_right = (ImageView) view.findViewById(R.id.iv_link_image_right);
            tv_link_title_left = (TextView) view.findViewById(R.id.tv_link_title_left);
            tv_link_title_right = (TextView) view.findViewById(R.id.tv_link_title_right);
            tv_link_content_left = (TextView) view.findViewById(R.id.tv_link_content_left);
            tv_link_content_right = (TextView) view.findViewById(R.id.tv_link_content_right);
        }
    }

}

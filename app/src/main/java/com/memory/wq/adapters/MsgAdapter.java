package com.memory.wq.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.caches.SmartImageView;
import com.memory.wq.properties.AppProperties;

import java.util.ArrayList;
import java.util.List;


public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MsgInfo> msgInfoList;
    private final SharedPreferences sp;
    private OnLinkClickListener listener;

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_LINK = 1;

    public MsgAdapter(Context context, List<MsgInfo> msgInfoList) {
        this.context = context;
        this.msgInfoList = msgInfoList;
        System.out.println("===========适配器222updateItem" + msgInfoList.size());
        sp = context.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
    }

    public void updateItem(List<MsgInfo> msgInfoList) {
        this.msgInfoList = new ArrayList<>(msgInfoList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return msgInfoList.get(position).getMsgType() == 0 ? TYPE_TEXT : TYPE_LINK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LINK) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_share_link_layout, parent, false);
            return new LinkViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_msg_layout, parent, false);
            return new TextViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgInfo msgInfo = msgInfoList.get(position);
        String currentEmail = sp.getString("email", "");

        if (TextUtils.isEmpty(currentEmail) || msgInfo == null) {
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
            handleTextMessage((TextViewHolder) holder, msgInfo, currentEmail);

        } else if (holder instanceof LinkViewHolder) {
            handleLinkMessage((LinkViewHolder) holder, msgInfo, currentEmail);
        }

    }

    private void handleLinkMessage(LinkViewHolder holder, MsgInfo msgInfo, String currentEmail) {
        String linkImageUrl = msgInfo.getLinkImageUrl();
        String linkTitle = msgInfo.getLinkTitle();
        //房间id
        String linkContent = msgInfo.getLinkContent();
        String content = msgInfo.getContent();

        if (msgInfo.getSenderEmail().equals(currentEmail)) {
            holder.ll_left_link.setVisibility(View.GONE);
            holder.ll_right_link.setVisibility(View.VISIBLE);


            holder.iv_link_image_right.setImageUrl(linkImageUrl);
            holder.tv_link_title_right.setText(linkTitle);
            holder.tv_link_content_right.setText(content);

            holder.siv_my_avatar_link.setImageUrl(msgInfo.getMyAvatarUrl());
        } else {

            holder.ll_left_link.setVisibility(View.VISIBLE);
            holder.ll_right_link.setVisibility(View.GONE);

            holder.iv_link_image_left.setImageUrl(linkImageUrl);
            holder.tv_link_title_left.setText(linkTitle);
            holder.tv_link_content_left.setText(content);

            holder.siv_friend_avatar_link.setImageUrl(msgInfo.getFriendAvatarUrl());
        }
        holder.ll_left_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(msgInfo);
            }
        });
        holder.ll_right_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(msgInfo);
            }
        });
    }

    private void handleTextMessage(TextViewHolder holder, MsgInfo msgInfo, String currentEmail) {
        if (msgInfo.getSenderEmail().equals(currentEmail)) {
            holder.ll_left.setVisibility(View.GONE);
            holder.ll_right.setVisibility(View.VISIBLE);
            holder.tv_rightMsg.setText(msgInfo.getContent());
        } else {
            holder.ll_left.setVisibility(View.VISIBLE);
            holder.ll_right.setVisibility(View.GONE);
            holder.tv_leftMsg.setText(msgInfo.getContent());
        }
        holder.siv_my_avatar.setImageUrl(msgInfo.getMyAvatarUrl());
        holder.siv_friend_avatar.setImageUrl(msgInfo.getFriendAvatarUrl());
    }


    @Override
    public int getItemCount() {
        return msgInfoList.size();
    }


    static class TextViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_left;
        LinearLayout ll_right;
        TextView tv_leftMsg;
        TextView tv_rightMsg;
        SmartImageView siv_friend_avatar;
        SmartImageView siv_my_avatar;

        public TextViewHolder(@NonNull View view) {
            super(view);
            ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
            ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
            tv_leftMsg = (TextView) view.findViewById(R.id.tv_leftMsg);
            tv_rightMsg = (TextView) view.findViewById(R.id.tv_rightMsg);
            siv_friend_avatar = (SmartImageView) view.findViewById(R.id.siv_friend_avatar);
            siv_my_avatar = (SmartImageView) view.findViewById(R.id.siv_my_avatar);
        }

    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_left_link;
        LinearLayout ll_right_link;
        SmartImageView siv_friend_avatar_link;
        SmartImageView siv_my_avatar_link;
        SmartImageView iv_link_image_left;
        SmartImageView iv_link_image_right;
        TextView tv_link_title_left;
        TextView tv_link_title_right;
        TextView tv_link_content_left;
        TextView tv_link_content_right;

        public LinkViewHolder(@NonNull View view) {
            super(view);
            ll_left_link = (LinearLayout) view.findViewById(R.id.ll_left_link);
            ll_right_link = (LinearLayout) view.findViewById(R.id.ll_right_link);
            siv_friend_avatar_link = (SmartImageView) view.findViewById(R.id.siv_friend_avatar_link);
            siv_my_avatar_link = (SmartImageView) view.findViewById(R.id.siv_my_avatar_link);
            iv_link_image_left = (SmartImageView) view.findViewById(R.id.iv_link_image_left);
            iv_link_image_right = (SmartImageView) view.findViewById(R.id.iv_link_image_right);
            tv_link_title_left = (TextView) view.findViewById(R.id.tv_link_title_left);
            tv_link_title_right = (TextView) view.findViewById(R.id.tv_link_title_right);
            tv_link_content_left = (TextView) view.findViewById(R.id.tv_link_content_left);
            tv_link_content_right = (TextView) view.findViewById(R.id.tv_link_content_right);
        }
    }

    public void setOnLinkClickListener(OnLinkClickListener listener) {
        this.listener = listener;
    }

    public interface OnLinkClickListener {
        void onClick(MsgInfo msgInfo);
    }
}

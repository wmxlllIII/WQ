package com.memory.wq.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.PostCommentInfo;
import com.memory.wq.interfaces.OnCommentActionListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.utils.TimeUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentAdapter extends ListAdapter<PostCommentInfo, PostCommentAdapter.CommentVH> {
    private static final String TAG = "WQ_PostCommentAdapter";

    private final OnCommentActionListener listener;

    public PostCommentAdapter(OnCommentActionListener listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostCommentAdapter.CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_comment_layout, parent, false);
        return new CommentVH(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH holder, int position) {
        PostCommentInfo commentInfo = getCurrentList().get(position);

        // 设置父评论数据
        Glide.with(holder.itemView.getContext())
//                .load(AppProperties.HTTP_SERVER_ADDRESS)
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .placeholder(R.mipmap.loading_default)
                .error(R.mipmap.loading_failure)
                .into(holder.iv_avatar);

        holder.tv_user.setText(commentInfo.getUserName());
        holder.tv_content.setText(commentInfo.getContent());

        holder.tv_time.setText(TimeUtils.convertTime(commentInfo.getTimestamp()));
        holder.tv_reply.setOnClickListener(view -> {
            if (listener != null) listener.onReplyToComment(commentInfo);
        });
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) listener.onReplyToComment(commentInfo);
        });

        if (commentInfo.getChildCommentList() != null && !commentInfo.getChildCommentList().isEmpty()) {
            holder.tv_toggle_replies.setVisibility(View.VISIBLE);
            holder.tv_toggle_replies.setText(commentInfo.isExpanded() ?
                    "收起回复" : "查看全部 " + commentInfo.getChildCommentList().size() + " 条回复");

            holder.rv_replies.setVisibility(commentInfo.isExpanded() ? View.VISIBLE : View.GONE);
            if (commentInfo.isExpanded()) {
                ReplyAdapter adapter = new ReplyAdapter(holder.itemView.getContext(), commentInfo.getChildCommentList(), listener);

                holder.rv_replies.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.rv_replies.setAdapter(adapter);
            }
            holder.tv_toggle_replies.setOnClickListener(v -> {
                commentInfo.setExpanded(!commentInfo.isExpanded());
                notifyItemChanged(position);
            });
        } else {
            holder.tv_toggle_replies.setVisibility(View.GONE);
            holder.rv_replies.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    static class CommentVH extends RecyclerView.ViewHolder {

        CircleImageView iv_avatar;
        TextView tv_user;
        TextView tv_time;
        TextView tv_content;
        TextView tv_reply;
        TextView tv_toggle_replies;
        RecyclerView rv_replies;

        public CommentVH(@NonNull View view) {
            super(view);
            iv_avatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
            tv_user = (TextView) view.findViewById(R.id.tv_user);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_reply = (TextView) view.findViewById(R.id.tv_reply);
            tv_toggle_replies = (TextView) view.findViewById(R.id.tv_toggle_replies);
            rv_replies = (RecyclerView) view.findViewById(R.id.rv_replies);
        }

    }

}

package com.memory.wq.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.beans.PostCommentInfo;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private Context context;
    private List<PostCommentInfo> replyList;
    private PostCommentAdapter.OnCommentActionListener listener;

    public ReplyAdapter(Context context, List<PostCommentInfo> replyList, PostCommentAdapter.OnCommentActionListener listener) {
        this.context = context;
        this.replyList = replyList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_reply, parent, false);
        return new ReplyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        PostCommentInfo reply = replyList.get(position);
        holder.tv_replyee.setText(reply.getUserName());
        holder.tv_replyer.setText(reply.getReplyToUserName());
        holder.tv_content.setText(reply.getContent());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReplyToComment(reply);
        });

        // 如果 reply 有子评论，递归显示
        if (reply.getChildCommentList() != null && !reply.getChildCommentList().isEmpty()) {
            holder.rv_sub_comment.setLayoutManager(new LinearLayoutManager(context));
            ReplyAdapter subAdapter = new ReplyAdapter(context, reply.getChildCommentList(),listener);
            holder.rv_sub_comment.setAdapter(subAdapter);
            holder.rv_sub_comment.setNestedScrollingEnabled(false);
            holder.rv_sub_comment.setVisibility(View.VISIBLE);
        } else {
            holder.rv_sub_comment.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return replyList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_replyee;
        TextView tv_replyer;
        TextView tv_content;
        RecyclerView rv_sub_comment;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_replyee = (TextView) itemView.findViewById(R.id.tv_replyee);
            tv_replyer = (TextView) itemView.findViewById(R.id.tv_replyer);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            rv_sub_comment = (RecyclerView) itemView.findViewById(R.id.rv_sub_comment);
        }
    }

}

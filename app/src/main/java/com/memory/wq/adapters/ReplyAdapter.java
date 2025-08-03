package com.memory.wq.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.beans.ReplyCommentInfo;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private Context context;
    private List<ReplyCommentInfo> replyList;
    private OnReplyClickListener listener;

    public ReplyAdapter(Context context, List<ReplyCommentInfo> replyList) {
        this.context = context;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_post_reply, null);
        return new ReplyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyCommentInfo reply = replyList.get(position);
        String text = reply.getUserName() + " 回复 @" + reply.getReplyToUser() + "：" + reply.getContent();
        holder.tv_reply.setText(text);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onClick(reply);
        });
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_reply;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_reply = (TextView) itemView.findViewById(R.id.tv_reply);
        }
    }

    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.listener = listener;
    }

    public interface OnReplyClickListener {
        void onClick(ReplyCommentInfo reply);
    }
}

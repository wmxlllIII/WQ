package com.memory.wq.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.beans.CommentInfo;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<CommentInfo> commentList;

    public CommentAdapter(Context context, List<CommentInfo> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView= View.inflate(context, R.layout.item_commment_layout, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        CommentInfo comment = commentList.get(position);
        viewHolder.tv_sender.setText(comment.getSender());
        viewHolder.tv_content.setText(comment.getContent());
        viewHolder.tv_timestamp.setText(comment.getTimestamp()+"");
        return convertView;
    }
    class ViewHolder{
        private TextView tv_sender;
        private TextView tv_content;
        private TextView tv_timestamp;

        public ViewHolder(View view) {
            tv_sender = (TextView) view.findViewById(R.id.tv_sender);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_timestamp = (TextView) view.findViewById(R.id.tv_timestamp);

        }

    }
}

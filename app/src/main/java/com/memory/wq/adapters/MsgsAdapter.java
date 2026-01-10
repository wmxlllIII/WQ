package com.memory.wq.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;

import java.util.List;

public class MsgsAdapter extends BaseAdapter {
    private Context context;
    private List<FriendInfo> friendInfoList;

    public MsgsAdapter(Context context, List<FriendInfo> friendInfoList) {
        this.context = context;
        this.friendInfoList = friendInfoList;
    }

    @Override
    public int getCount() {
        return friendInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_msgs_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendInfo friendInfo = friendInfoList.get(position);
        Glide.with(parent.getContext())
                .load(friendInfo.getAvatarUrl())
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .into(viewHolder.iv_avatar);

        viewHolder.tv_nickname.setText(friendInfo.getNickname());
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_avatar;
        TextView tv_nickname;

        public ViewHolder(View view) {
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
        }
    }
}

package com.memory.wq.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.caches.SmartImageView;

import java.util.List;

public class SearchFriendAdapter extends BaseAdapter {
    public static final String TAG=SearchFriendAdapter.class.getName();

    private Context context;
    private List<FriendInfo> friendInfoList;

    public SearchFriendAdapter(Context context, List<FriendInfo> friendInfoList) {
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
        if (convertView==null){
            convertView= View.inflate(context,R.layout.item_search_friend,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        FriendInfo friendInfo = friendInfoList.get(position);
        viewHolder.siv_avatar.setImageUrl(friendInfo.getAvatarUrl());
        Log.d(TAG, "=====getView: 昵称"+friendInfo.getNickname()+"====头像"+friendInfo.getAvatarUrl());
        viewHolder.tv_nickName.setText(friendInfo.getNickname());
        return convertView;
    }

    static class ViewHolder {
        SmartImageView siv_avatar;
        TextView tv_nickName;

        public ViewHolder(View view) {
            siv_avatar = (SmartImageView) view.findViewById(R.id.siv_avatar_aaa);
            tv_nickName = (TextView) view.findViewById(R.id.tv_nickName);
        }
    }
}

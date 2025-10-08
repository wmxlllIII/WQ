package com.memory.wq.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.memory.wq.R;

import com.memory.wq.beans.FriendRelaInfo;
import com.memory.wq.caches.SmartImageView;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class FriendRelaAdapter extends BaseAdapter {
    public static final String TAG = FriendRelaAdapter.class.getName();

    private Context context;
    private List<FriendRelaInfo> reqInfoList;
    private String email;
    private UpdateRelaListener listener;


    public FriendRelaAdapter(Context context, List<FriendRelaInfo> reqInfoList) {
        this.context = context;
        this.reqInfoList = reqInfoList;
        email = context.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE).getString("email", "");
    }

    public void updateAdapterDate(List<FriendRelaInfo> friendRelaList) {
        if (friendRelaList != null && friendRelaList.size() > 0) {
            this.reqInfoList.clear();
            this.reqInfoList.addAll(friendRelaList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return reqInfoList.size();
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
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_friend_request_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FriendRelaInfo friendReqInfo = reqInfoList.get(position);

        boolean isReceiver = friendReqInfo.getTargetEmail().equals(email);


        viewHolder.siv_avatar.setImageUrl(isReceiver ? friendReqInfo.getSourceAvatarUrl() : friendReqInfo.getTargetAvatarUrl());
        viewHolder.tv_nickname.setText(isReceiver ? friendReqInfo.getSourceNickname() : friendReqInfo.getTargetNickname());
        viewHolder.tv_verify_message.setText(friendReqInfo.getValidMsg());


        if (isReceiver) {
            if ("sended".equals(friendReqInfo.getState())) {
                viewHolder.tv_friend_state.setVisibility(View.GONE);
                viewHolder.btn_accept.setVisibility(View.VISIBLE);
                viewHolder.btn_reject.setVisibility(View.VISIBLE);

            } else {
                viewHolder.tv_friend_state.setVisibility(View.VISIBLE);
                viewHolder.btn_accept.setVisibility(View.GONE);
                viewHolder.btn_reject.setVisibility(View.GONE);
                viewHolder.tv_friend_state.setText(friendReqInfo.getState().equals("accepted") ? "已添加" : "已拒绝");
            }
        } else {
            if ("sended".equals(friendReqInfo.getState())) {
                viewHolder.tv_friend_state.setVisibility(View.VISIBLE);
                viewHolder.btn_accept.setVisibility(View.GONE);
                viewHolder.btn_reject.setVisibility(View.GONE);
                viewHolder.tv_friend_state.setText("已申请");

            } else {
                viewHolder.tv_friend_state.setVisibility(View.VISIBLE);
                viewHolder.btn_accept.setVisibility(View.GONE);
                viewHolder.btn_reject.setVisibility(View.GONE);
                viewHolder.tv_friend_state.setText("accepted".equals(friendReqInfo.getState()) ? "已添加" : "待验证");
            }
        }

        viewHolder.btn_accept.setOnClickListener(v -> {
            MsgManager.updateRela(context, true, friendReqInfo.getSourceEmail(), new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    Log.d(TAG, "onSuccess: =======" + result);
                    listener.onUpdateRelaSuccess();
                }

                @Override
                public void onError(String err) {

                }
            });

        });

        viewHolder.btn_reject.setOnClickListener(v -> {
            MsgManager.updateRela(context, false, friendReqInfo.getSourceEmail(), new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (listener != null)
                        listener.onUpdateRelaSuccess();
                }

                @Override
                public void onError(String err) {

                }
            });
        });


        return convertView;
    }


    private static class ViewHolder {
        SmartImageView siv_avatar;
        TextView tv_nickname;
        TextView tv_verify_message;
        ImageButton btn_accept;
        ImageButton btn_reject;
        TextView tv_friend_state;

        private ViewHolder(View convertView) {
            siv_avatar = (SmartImageView) convertView.findViewById(R.id.siv_avatar);
            tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
            tv_verify_message = (TextView) convertView.findViewById(R.id.tv_verify_message);
            btn_accept = (ImageButton) convertView.findViewById(R.id.btn_accept);
            btn_reject = (ImageButton) convertView.findViewById(R.id.btn_reject);
            tv_friend_state = (TextView) convertView.findViewById(R.id.tv_friend_state);
        }
    }

    public interface UpdateRelaListener {
        void onUpdateRelaSuccess();
    }

    public void setUpdateRelaListener(UpdateRelaListener listener) {
        this.listener = listener;
    }
}

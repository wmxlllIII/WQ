package com.memory.wq.fragment;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.activities.FriendRelaActivity;
import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.MainActivity;
import com.memory.wq.activities.MsgActivity;
import com.memory.wq.activities.SearchFriendActivity;
import com.memory.wq.adapters.FriendAdapter;
import com.memory.wq.adapters.MsgsAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.SessionManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MessageFragment extends Fragment implements View.OnClickListener, WebService.WebSocketListener, ResultCallback<List<FriendInfo>> {

    private RecyclerView rv_friends;
    private ListView lv_msg;
    private FriendAdapter friendAdapter;
    private List<FriendInfo> friendInfoList;
    private SharedPreferences sp;
    private FriendManager friendManager;

    private final EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_MSG, EventType.EVENT_TYPE_REQUEST_FRIEND);
    private WebService msgService;
    private MsgConn msgConn;
    private MsgsAdapter msgsAdapter;
    private AppCompatActivity mActivity;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_layout, container, false);
        initView(view);
        if (!SessionManager.isLoggedIn(mActivity)) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("未登录")
                    .setMessage("登录后即可体验完整功能哦~")
                    .setIcon(R.mipmap.ic_bannertest2)
                    .setNegativeButton("去登录", (dialogInterface, i) -> {
                        startActivity(new Intent(mActivity, LaunchActivity.class));
                        getActivity().finish();
                    })
                    .setPositiveButton("取消", (dialogInterface, i) -> {
                        view.findViewById(R.id.ll_already_login).setVisibility(View.GONE);
                        view.findViewById(R.id.layout_no_login).setVisibility(View.VISIBLE);
                    })
                    .setCancelable(false)
                    .show();
            return view;
        }

        initData();
        show();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            mActivity = (MainActivity) getContext();
        }
    }

    private void show() {
        String token = sp.getString("token", "");
        friendManager.getAllFriends(mActivity, token, this);

    }

    private void initData() {
        friendInfoList = new ArrayList<>();

        //  false,true -1  2,1

//        Collections.sort(friendInfoList, new Comparator<FriendInfo>() {
//            @Override
//            public int compare(FriendInfo o1, FriendInfo o2) {
//                return Boolean.compare(o2.isOnline(),o1.isOnline());
//            }
//        });
        sp = mActivity.getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        friendManager = new FriendManager();

        rv_friends.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        friendAdapter = new FriendAdapter(mActivity, friendInfoList);
        rv_friends.setAdapter(friendAdapter);


        Intent intent = new Intent(mActivity, WebService.class);
        msgConn = new MsgConn();
        mActivity.startService(intent);
        mActivity.bindService(intent, msgConn, Context.BIND_AUTO_CREATE);

    }

    private void initView(View view) {
        RelativeLayout iv_add = view.findViewById(R.id.iv_add);
        RelativeLayout iv_search = view.findViewById(R.id.iv_search);
        rv_friends = view.findViewById(R.id.rv_friends);
        lv_msg = view.findViewById(R.id.lv_msg);

        iv_add.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        lv_msg.setOnItemClickListener((parent, view1, position, id) -> {
            FriendInfo friendInfo = (FriendInfo) parent.getItemAtPosition(position);
            Intent intent = new Intent(mActivity, MsgActivity.class);
            intent.putExtra(AppProperties.FRIENDINFO, friendInfo);
            mActivity.startActivity(intent);
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                startActivity(new Intent(mActivity, FriendRelaActivity.class));
                break;
            case R.id.iv_search:
                startActivity(new Intent(mActivity, SearchFriendActivity.class));
                break;

        }
    }

    @Override
    public void onSuccess(List<FriendInfo> friendInfoList) {
        mActivity.runOnUiThread(() -> {
            if (friendAdapter != null) {
                friendAdapter.updateList(friendInfoList);
                this.friendInfoList = friendInfoList;
                msgsAdapter = new MsgsAdapter(mActivity, friendInfoList);
                lv_msg.setAdapter(msgsAdapter);
            }

        });
    }

    @Override
    public void onError(String err) {

    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        switch (eventType) {
            case EVENT_TYPE_REQUEST_FRIEND:

                break;
            case EVENT_TYPE_MSG:

                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    class MsgConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((IWebSocketService) service).getService();
            msgService.registerListener(MessageFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgService.unregisterListener(MessageFragment.this);
        }
    }
}

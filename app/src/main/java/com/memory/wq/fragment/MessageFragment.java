package com.memory.wq.fragment;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.memory.wq.R;
import com.memory.wq.activities.MsgActivity;
import com.memory.wq.activities.SearchFriendActivity;
import com.memory.wq.activities.FriendRelaActivity;
import com.memory.wq.adapters.FriendAdapter;
import com.memory.wq.adapters.MsgsAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MessageFragment extends Fragment implements View.OnClickListener , WebService.WebSocketListener, ResultCallback<List<FriendInfo>> {

    private View view;
    private RecyclerView rv_friends;
    private ListView lv_msg;
    private RelativeLayout iv_add;
    private RelativeLayout iv_search;
    private FriendAdapter friendAdapter;
    private List<FriendInfo> friendInfoList;
    private SharedPreferences sp;
    private FriendManager friendManager;

    private EnumSet<EventType> eventTypes=EnumSet.of(EventType.EVENT_TYPE_MSG,EventType.EVENT_TYPE_REQUEST_FRIEND);
    private WebService msgService;
    private MsgConn msgConn;
    private MsgsAdapter msgsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_layout, null, false);
        initView(view);
        initData();
        show();
        return view;
    }

    private void show() {
        String token = sp.getString("token", "");
        friendManager.getAllFriends(getContext(),token,this);

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
        sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        friendManager = new FriendManager();

        rv_friends.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        friendAdapter = new FriendAdapter(getContext(),friendInfoList);
        rv_friends.setAdapter(friendAdapter);



        Intent intent = new Intent(getContext(), WebService.class);
        msgConn = new MsgConn();
        getContext().startService(intent);
        getContext().bindService(intent,msgConn,Context.BIND_AUTO_CREATE);

    }

    private void initView(View view) {
        iv_add = (RelativeLayout) view.findViewById(R.id.iv_add);
        iv_search = (RelativeLayout) view.findViewById(R.id.iv_search);
        rv_friends = (RecyclerView) view.findViewById(R.id.rv_friends);
        lv_msg = (ListView) view.findViewById(R.id.lv_msg);

        iv_add.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = (FriendInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), MsgActivity.class);
                intent.putExtra(AppProperties.FRIENDINFO,friendInfo);
                getContext().startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_add:
                startActivity(new Intent(getContext(), FriendRelaActivity.class));
                break;
            case R.id.iv_search:
                startActivity(new Intent(getContext(), SearchFriendActivity.class));
                break;

        }
    }

    @Override
    public void onSuccess(List<FriendInfo> friendInfoList) {
        ((Activity)getContext()).runOnUiThread(()->{
            if (friendAdapter!=null){
                friendAdapter.updateList(friendInfoList);
                this.friendInfoList=friendInfoList;
                msgsAdapter = new MsgsAdapter(getContext(), friendInfoList);
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
        switch (eventType){
            case EVENT_TYPE_REQUEST_FRIEND:

                break;
            case EVENT_TYPE_MSG:

                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }
    class MsgConn implements ServiceConnection{

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

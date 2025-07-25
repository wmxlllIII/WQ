package com.memory.wq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.memory.wq.R;

import com.memory.wq.adapters.FriendRelaAdapter;
import com.memory.wq.beans.FriendRelaInfo;

import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class FriendRelaActivity extends AppCompatActivity implements View.OnClickListener, WebService.WebSocketListener, ResultCallback<List<FriendRelaInfo>>, FriendRelaAdapter.UpdateRelaListener {

    private String token;

    private WebService webService;
    private MyConn conn;
    private boolean isBind = false;
    private final EnumSet<EventType> EVENT_TYPE_SET = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private ListView lv_friendReq;
    private List<FriendRelaInfo> friendRelaList = new ArrayList<>();
    private FriendRelaAdapter adapter;
    private MsgManager msgManager;
    private SharedPreferences sp;
    private LinearLayout ll_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wsactivity);
        initView();
        initData();

        System.out.println("===========TestWSActivity====token" + token);
        showLV();
    }

    private void initData() {
        Intent intent = new Intent(this, WebService.class);
        startService(intent);
        conn = new MyConn();
        msgManager = new MsgManager(this);
        bindService(intent, conn, BIND_AUTO_CREATE);

        if (adapter == null) {
            adapter = new FriendRelaAdapter(this, friendRelaList);
            adapter.setUpdateRelaListener(this);
        }

        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");

    }

    private void showLV() {

        lv_friendReq.setAdapter(adapter);
        msgManager.getAllRelation(this,false, AppProperties.FRIEND_RELATIONSHIP, token,this);

        //1从数据库拿-----数据每5分钟轮询get
    }

    private void initView() {
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        lv_friendReq = (ListView) findViewById(R.id.lv_friendReq);

        ll_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                startActivity(new Intent(this,SearchUserActivity.class));
        }
    }


    @Override
    public EnumSet<EventType> getEvents() {
        return EVENT_TYPE_SET;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        switch (eventType) {
            case EVENT_TYPE_REQUEST_FRIEND:
                runOnUiThread(() -> {
                    msgManager.getAllRelation(this,true, AppProperties.FRIEND_RELATIONSHIP, token,this);
                });
                break;
        }

    }


    @Override
    public void onConnectionChanged(boolean isConnected) {
        runOnUiThread(() -> {
            //
        });
    }

    @Override
    public void onSuccess(List<FriendRelaInfo> result) {
        runOnUiThread(() -> {
            if (adapter != null) {
                adapter.updateAdapterDate(result);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onError(String err) {

    }


    @Override
    public void onUpdateRelaSuccess() {
        msgManager.getAllRelation(this,true, AppProperties.FRIEND_RELATIONSHIP, token,this);
    }


    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webService = ((IWebSocketService) service).getService();
            webService.registerListener(FriendRelaActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            webService.unregisterListener(FriendRelaActivity.this);
            webService = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            webService.unregisterListener(this);
            unbindService(conn);

        }
    }
}
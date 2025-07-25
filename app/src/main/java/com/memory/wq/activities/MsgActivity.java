package com.memory.wq.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.adapters.MsgAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RoomInfo;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ShareConfirmDialog;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MsgActivity extends BaseActivity implements View.OnClickListener, WebService.WebSocketListener, ResultCallback<List<MsgInfo>>, MsgAdapter.OnLinkClickListener {
    public static final String TAG = MsgActivity.class.getName();

    private EditText et_inputText;
    private Button btn_send;
    private RecyclerView rv_msg;
    private EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_MSG);
    private MsgAdapter adapter;
    private List<MsgInfo> msgInfoList = new ArrayList<>();
    private WebService msgService;
    private MsgConn msgConn;
    private MsgManager msgManager;

    private String token;
    private SharedPreferences sp;
    private FriendInfo friendInfo;
    private TextView tv_nickname;
    private MsgInfo linkInfo;
    private MovieManager movieManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        initView();
        initData();
        show();
    }

    private void initData() {
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        adapter = new MsgAdapter(this, msgInfoList);
        adapter.setOnLinkClickListener(this);
        Intent intent = new Intent(this, WebService.class);
        if (msgConn == null)
            msgConn = new MsgConn();
        bindService(intent, msgConn, BIND_AUTO_CREATE);
        msgManager = new MsgManager(this);

        Intent intent1 = getIntent();
        friendInfo = (FriendInfo) intent1.getSerializableExtra(AppProperties.FRIENDINFO);
        if (intent1.hasExtra(AppProperties.SHARE_MESSAGE)) {
            linkInfo = (MsgInfo) intent1.getSerializableExtra(AppProperties.SHARE_MESSAGE);
            movieManager = new MovieManager();
            showShareUI();
        }

        msgManager.getAllMsg(friendInfo.getEmail(), this);
    }

    private void showShareUI() {
        Log.d(TAG, "onShare: ===分享信息" + friendInfo.toString() + "aaa+++" + linkInfo.toString());

        ShareConfirmDialog dialog = new ShareConfirmDialog(this, friendInfo, linkInfo, new ShareConfirmDialog.OnShareActionListener() {
            @Override
            public void onShare(MsgInfo shareMsg) {

                movieManager.shareRoom(MsgActivity.this, token, shareMsg, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(() -> {
                            MyToast.showToast(MsgActivity.this, "分享成功");
                            finish();
                        });
                    }

                    @Override
                    public void onError(String err) {
                        runOnUiThread(() -> {
                            MyToast.showToast(MsgActivity.this, "分享失败");
                            finish();
                        });
                    }
                });
            }

            @Override
            public void onCancel() {
                MyToast.showToast(MsgActivity.this, "用户取消分享");
                finish();
            }
        });
        dialog.show();

    }

    private void show() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rv_msg.setLayoutManager(layoutManager);
        rv_msg.setAdapter(adapter);
        tv_nickname.setText(friendInfo.getNickname());
    }

    private void initView() {
        et_inputText = (EditText) findViewById(R.id.et_inputText);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        rv_msg = (RecyclerView) findViewById(R.id.rv_msg);


        btn_send.setOnClickListener(this);
    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        switch (eventType) {
            case EVENT_TYPE_MSG:
                msgManager.getAllMsg(friendInfo.getEmail(), new ResultCallback<List<MsgInfo>>() {
                    @Override
                    public void onSuccess(List<MsgInfo> result) {
                        runOnUiThread(() -> {

                            adapter.updateItem(result);
                            if (msgInfoList.size() > 0) {
                                Log.d(TAG, "===1正在滚动到最后位置: " + (msgInfoList.size() - 1));
                                rv_msg.post(() -> {
                                    rv_msg.smoothScrollToPosition(msgInfoList.size() - 1);
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String err) {

                    }
                });
                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onSuccess(List<MsgInfo> msgInfoList) {
        runOnUiThread(() -> {
            adapter.updateItem(msgInfoList);
            Log.d(TAG, "onSuccess: ===滚动到最后位置" + (msgInfoList.size() - 1));
            rv_msg.smoothScrollToPosition(msgInfoList.size() - 1);

        });
    }

    @Override
    public void onError(String err) {
        //获取本地消息记录失败回调
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendMsg();
                break;
            case R.id.tv_cancel:
                break;
            case R.id.tv_share:
                break;
        }
    }

    private void sendMsg() {
        String msg = et_inputText.getText().toString().trim();
        if (TextUtils.isEmpty(msg))
            return;

        String currentEmail = sp.getString("email", "");


        msgManager.sendMsg(token, currentEmail, friendInfo.getEmail(), msg, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                msgManager.getAllMsg(friendInfo.getEmail(), MsgActivity.this);
            }

            @Override
            public void onError(String err) {

            }
        });
        et_inputText.setText("");
    }

    @Override
    public void onClick(MsgInfo msgInfo) {
        Intent intent = new Intent(this, AudioActivity.class);
        intent.putExtra(AppProperties.ROLE_TYPE, RoleType.ROLE_TYPE_AUDIENCE);
        intent.putExtra(AppProperties.ROOM_ID, msgInfo.getLinkContent());
        startActivity(intent);
        System.out.println("=====加入房间id" + msgInfo.getLinkContent());
    }

    class MsgConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgService = ((IWebSocketService) service).getService();
            msgService.registerListener(MsgActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgService.unregisterListener(MsgActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(msgConn);
        msgService.unregisterListener(this);
    }
}
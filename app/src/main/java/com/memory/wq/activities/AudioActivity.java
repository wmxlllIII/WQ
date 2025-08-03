package com.memory.wq.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.adapters.MovieCommentAdapter;
import com.memory.wq.adapters.ShareFriendsAdapter;
import com.memory.wq.beans.MovieCommentInfo;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RtcInfo;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.managers.AgoraManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.managers.TokenManager;
import com.memory.wq.properties.AppProperties;

import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareFunctionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.mediaplayer.Constants;

public class AudioActivity extends BaseActivity implements View.OnClickListener, AgoraManager.AgoraEventListener {
    private static final String TAG = AudioActivity.class.getName();

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private List<MovieCommentInfo> commentList = new ArrayList<>();


    private ImageView btn_play_pause;
    private SeekBar seekBar;
    private TextView tv_status;
    private ListView lv_comment;
    private List<FriendInfo> friendList = new ArrayList<>();

    private AgoraManager agoraManager;


    private RoleType roleType;
    private String roomId;
    private MovieManager movieManager;
    private PermissionManager permissionManager;
    private String userId;
    private String token;
    private Button btn_send;
    private EditText et_comment;
    private MovieCommentAdapter adapter;
    private RelativeLayout rl_controllers;
    private RelativeLayout rl_video_container;
    private RelativeLayout rl_portrait_layout;
    private ImageView iv_landscape;
    private boolean isFullScreen = false;
    private FrameLayout remote_video_view_container;

    private ViewGroup.LayoutParams rl_video_containerParams;
    private ViewGroup.LayoutParams rl_controllersParams;
    private ViewGroup.LayoutParams remote_video_view_containerParams;
    private TextView tv_title;
    private ImageView iv_menu;
    private MovieInfo movieInfo;
    private RelativeLayout rl_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
        initCommentList();
        initPermissions();
    }

    private void initCommentList() {
        adapter = new MovieCommentAdapter(this, commentList);
        lv_comment.setAdapter(adapter);
    }

    private void initPermissions() {
        permissionManager = new PermissionManager(this);
        if (!checkSelfPermissions()) {
            permissionManager.requestPermission(REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        } else {
            initData();
        }
    }

    private void initData() {

        SharedPreferences sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        userId = sp.getString("userId", "");

        Intent intent = getIntent();
        roleType = (RoleType) intent.getSerializableExtra(AppProperties.ROLE_TYPE);
        roomId = roleType == RoleType.ROLE_TYPE_BROADCASTER ? userId : intent.getStringExtra(AppProperties.ROOM_ID);


        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            movieInfo = (MovieInfo) intent.getSerializableExtra(AppProperties.MOVIE_PATH);
            movieManager = new MovieManager();
            movieManager.saveRoom(token, userId, movieInfo.getMovieId());
        }

        TokenManager tokenManager = new TokenManager();
        int role = roleType == RoleType.ROLE_TYPE_BROADCASTER ? 1 : 2;
        tokenManager.getToken(userId, token, roomId, role, new ResultCallback<RtcInfo>() {
            @Override
            public void onSuccess(RtcInfo result) {
                runOnUiThread(() -> {
                    initAgoraManager(result);
                });
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "onError: ===获取声网token失败" + err);
            }
        });

    }

    private void initAgoraManager(RtcInfo rtcInfo) {
        agoraManager = new AgoraManager(this, userId, roomId, rtcInfo);
        agoraManager.setEventListener(this);

        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            agoraManager.setMoviePath(movieInfo.getMovieUrl());
        }

        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            setBroadcasterUI();
            tv_status.setText("准备媒体中...");
            agoraManager.prepareMediaPlayer();
        } else {
            setAudienceUI();
            tv_status.setText("准备加入...");
        }
        agoraManager.loginRtm(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                runOnUiThread(() -> {
                    if (roleType == RoleType.ROLE_TYPE_AUDIENCE) {
                        tv_status.setText("等待主播视频流...");
                        agoraManager.joinChannel(false);
                    } else {
                        tv_status.setText("媒体加载中...");
                    }
                });
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "onError: ===登录rtm错误" + err);
            }
        });
    }

    private void setBroadcasterUI() {
        Log.d(TAG, "setBroadcasterUI: ===设置主播端ui");
        SurfaceView localView = new SurfaceView(this);
        localView.setZOrderMediaOverlay(true);
        remote_video_view_container.addView(localView);
        agoraManager.setupLocalVideo(localView);
        btn_play_pause.setEnabled(true);
        seekBar.setEnabled(true);
    }


    private void setAudienceUI() {
        btn_play_pause.setEnabled(false);
        seekBar.setEnabled(false);
        tv_status.setText("等待主播加入...");
    }

    private boolean checkSelfPermissions() {
        for (String permission : REQUESTED_PERMISSIONS) {
            if (permissionManager.isPermitPermission(permission)) {
                return false;
            }
        }
        return true;
    }


    private void initView() {
        btn_play_pause = (ImageView) findViewById(R.id.btn_play_pause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_status = (TextView) findViewById(R.id.tv_status);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_comment = (EditText) findViewById(R.id.et_comment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);

        remote_video_view_container = (FrameLayout) findViewById(R.id.remote_video_view_container);
        remote_video_view_containerParams = remote_video_view_container.getLayoutParams();

        rl_controllers = (RelativeLayout) findViewById(R.id.rl_controllers);
        rl_controllersParams = rl_controllers.getLayoutParams();

        rl_video_container = (RelativeLayout) findViewById(R.id.rl_video_container);
        rl_video_containerParams = rl_video_container.getLayoutParams();

        rl_portrait_layout = (RelativeLayout) findViewById(R.id.rl_portrait_layout);
        iv_landscape = (ImageView) findViewById(R.id.iv_landscape);

        remote_video_view_container.setOnClickListener(this);
        rl_controllers.setVisibility(View.GONE);
        btn_play_pause.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        iv_landscape.setOnClickListener(this);
        iv_menu.setOnClickListener(this);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && agoraManager != null) {
                    agoraManager.seek((long) (progress * 1000));

                    if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
                        agoraManager.sendSyncCommand(progress, System.currentTimeMillis());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                initView();
                initData();
            } else {
                MyToast.showToast(this, "需要授予所有权限才能使用该功能");
                finish();
            }
        }
    }


    @Override
    public void onRemoteUserJoined(int uid) {

        runOnUiThread(() -> {
            if (roleType == RoleType.ROLE_TYPE_AUDIENCE) {
                tv_status.setText("已连接房主: " + uid);

                SurfaceView remoteView = new SurfaceView(this);
                remote_video_view_container.removeAllViews();
                remote_video_view_container.addView(remoteView);
                agoraManager.setupRemoteVideo(uid, remoteView);
            }
        });
    }

    @Override
    public void onRtmMessageReceived(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String cmd = json.getString("cmd");

            switch (cmd) {
                case "play":
                    runOnUiThread(() -> tv_status.setText("播放中"));
                    break;
                case "pause":
                    runOnUiThread(() -> tv_status.setText("已暂停"));
                    break;
                case "sync":
                    int progress = json.getInt("progress");
//                    long timestamp = json.getLong("timestamp");
                    runOnUiThread(() -> {
                        if (Math.abs(seekBar.getProgress() - progress) > 2) {
                            seekBar.setProgress(progress);
                        }
                    });
                    break;
                case "comment":
                    String sender = json.getString("sender");
                    String content = json.getString("content");
                    long timestamp = json.getLong("timestamp");
                    runOnUiThread(() -> {
                        if (!userId.equals(sender)) {
                            addComment(sender, content, timestamp);
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, "onRtmMessageReceived: ===解析RTM消息错误" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onMediaStateChanged(Constants.MediaPlayerState state) {
        Log.d(TAG, "onMediaStateChanged: ===媒体状态改变" + state);
        if (roleType != RoleType.ROLE_TYPE_BROADCASTER)
            return;
        if (state == Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
            runOnUiThread(() -> {
                setBroadcasterUI();
                agoraManager.joinChannel(true);
                tv_status.setText("等待观众加入...");
            });

        }
    }

    @Override
    public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
        // 首次获取到视频尺寸时计算宽高比
        Log.d(TAG, "onFirstRemoteVideoFrame: ===视频chicun:" + width + "==高:" + height);
    }

    @Override
    public void onPlaybackProgress(int progress) {
        runOnUiThread(() -> {
            if (Math.abs(seekBar.getProgress() - progress) > 2) {
                seekBar.setProgress(progress);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                switchPlayPause();
                break;
            case R.id.btn_send:
                sendComment();
                break;
            case R.id.remote_video_view_container:
                adjustControlBar();
                break;
            case R.id.iv_landscape:
                switchOrientation();
                break;
            case R.id.iv_menu:
                showOptions();
                break;
        }

    }

    private void switchPlayPause() {
        if (agoraManager.isPlaying()) {
            agoraManager.pause();
            agoraManager.sendControlCommand("pause");
            tv_status.setText("已暂停");
        } else {
            agoraManager.play();
            agoraManager.sendControlCommand("play");
            tv_status.setText("播放中");
        }
    }

    private void sendComment() {
        String comment = et_comment.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            addComment(userId, comment, System.currentTimeMillis());
            agoraManager.sendComment(comment);
            et_comment.setText("");
        }
    }

    private void adjustControlBar() {
        if (rl_controllers.getVisibility() == View.GONE) {
            rl_controllers.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                if (rl_controllers.getVisibility() == View.VISIBLE) {
                    rl_controllers.setVisibility(View.GONE);
                }
            }, 3000);
        } else {
            rl_controllers.setVisibility(View.GONE);
        }
    }


    private void showOptions() {
        new FriendManager().getAllFriends(this, token, new ResultCallback<List<FriendInfo>>() {
            @Override
            public void onSuccess(List<FriendInfo> result) {
                friendList.clear();
                friendList.addAll(result);
                showShareFunctionMenu();
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void showShareFunctionMenu() {
        ShareFunctionMenu functionMenu = new ShareFunctionMenu(this, new String[]{"分享给好友"}, friendList);
        functionMenu.setOnOptionSelectedListener(new ShareFunctionMenu.OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(String option) {
                MyToast.showToast(AudioActivity.this, "点击了" + option);

            }
        });
        functionMenu.setOnFriendClickedListener(new ShareFriendsAdapter.OnFriendClickListener() {
            @Override
            public void onFriendClick(FriendInfo friendInfo) {
                shareRoomLinkToFriend(friendInfo);
                functionMenu.dismiss();
            }
        });
        functionMenu.show();
    }

    private void shareRoomLinkToFriend(FriendInfo friendInfo) {
        MsgInfo shareMsg = new MsgInfo();
        shareMsg.setMsgType(1);
        shareMsg.setLinkTitle("加入共享房间");
        shareMsg.setContent("点击链接加入观看" + movieInfo.getTitle());
        shareMsg.setLinkContent(roomId);

        shareMsg.setLinkImageUrl(movieInfo.getCoverUrl());
        shareMsg.setSenderEmail(SPManager.getUserInfo(this).getEmail());
        shareMsg.setReceiverEmail(friendInfo.getEmail());

        Intent intent = new Intent(AudioActivity.this, MsgActivity.class);
        intent.putExtra(AppProperties.FRIENDINFO, friendInfo);
        intent.putExtra(AppProperties.SHARE_MESSAGE, shareMsg);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (isFullScreen) {
            switchOrientation();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreenMode();
        } else {
            exitFullScreenMode();
        }
    }

    private void enterFullScreenMode() {
        try {
            Log.d(TAG, "enterFullScreenMode: ===进入全屏");
            isFullScreen = true;
            rl_portrait_layout.setVisibility(View.GONE);
            rl_title.setVisibility(View.GONE);


            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            remote_video_view_container.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams controllerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            controllerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rl_controllers.setLayoutParams(controllerParams);

            iv_landscape.setImageResource(R.mipmap.wyf);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "enterFullScreenMode: ===进入全屏模式错误" + e.getMessage());
        }
    }

    private void exitFullScreenMode() {
        try {
            Log.d(TAG, "exitFullScreenMode: ===退出全屏");
            isFullScreen = false;
            rl_portrait_layout.setVisibility(View.VISIBLE);
            rl_title.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            remote_video_view_container.setLayoutParams(remote_video_view_containerParams);
            rl_controllers.setLayoutParams(rl_controllersParams);

            iv_landscape.setImageResource(R.mipmap.wyf);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "exitFullScreenMode: ===退出全屏模式错误" + e.getMessage());
        }
    }


    private void switchOrientation() {
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


    }

    private void addComment(String userId, String comment, long timestamp) {
        MovieCommentInfo movieCommentInfo = new MovieCommentInfo();
        movieCommentInfo.setContent(comment);
        movieCommentInfo.setSender(userId);
        movieCommentInfo.setTimestamp(timestamp);
        commentList.add(movieCommentInfo);
        adapter.notifyDataSetChanged();
        lv_comment.smoothScrollToPosition(commentList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agoraManager != null) {
            agoraManager.leaveChannel();
            agoraManager.destroy();
        }
        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            MovieManager movieManager = new MovieManager();
            movieManager.releaseRoom(token, roomId);
        }
    }

}
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.adapters.MovieCommentAdapter;
import com.memory.wq.adapters.ShareFriendsAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MovieCommentInfo;
import com.memory.wq.beans.MovieInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.RtcInfo;
import com.memory.wq.databinding.ActivityAudioBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.managers.AgoraManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.SPManager;
import com.memory.wq.managers.TokenManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareFunctionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.mediaplayer.Constants;

public class AudioActivity extends BaseActivity<ActivityAudioBinding> implements View.OnClickListener, AgoraManager.AgoraEventListener {
    private static final String TAG = AudioActivity.class.getName();

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final List<MovieCommentInfo> commentList = new ArrayList<>();
    private final List<FriendInfo> friendList = new ArrayList<>();

    private AgoraManager mAgoraManager;
    private RoleType roleType;
    private String mRoomId;
    private MovieManager mMovieManager;
    private PermissionManager mPermissionManager;
    private String mUserId;
    private String token;
    private MovieCommentAdapter mAdapter;
    private boolean isFullScreen = false;

    private ViewGroup.LayoutParams rl_video_containerParams;
    private ViewGroup.LayoutParams rl_controllersParams;
    private ViewGroup.LayoutParams remote_video_view_containerParams;
    private MovieInfo movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initCommentList();
        initPermissions();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio;
    }

    private void initCommentList() {
        mAdapter = new MovieCommentAdapter(this, commentList);
        mBinding.lvComment.setAdapter(mAdapter);
    }

    private void initPermissions() {
        mPermissionManager = new PermissionManager(this);
        if (!checkSelfPermissions()) {
            mPermissionManager.requestPermission(REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        } else {
            initData();
        }
    }

    private void initData() {

        SharedPreferences sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        mUserId = sp.getString("userId", "");

        Intent intent = getIntent();
        roleType = (RoleType) intent.getSerializableExtra(AppProperties.ROLE_TYPE);
        mRoomId = roleType == RoleType.ROLE_TYPE_BROADCASTER ? mUserId : intent.getStringExtra(AppProperties.ROOM_ID);


        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            movieInfo = (MovieInfo) intent.getSerializableExtra(AppProperties.MOVIE_PATH);
            mMovieManager = new MovieManager();
            mMovieManager.saveRoom(token, mUserId, movieInfo.getMovieId());
        }

        TokenManager tokenManager = new TokenManager();
        int role = roleType == RoleType.ROLE_TYPE_BROADCASTER ? 1 : 2;
        tokenManager.getToken(mUserId, token, mRoomId, role, new ResultCallback<RtcInfo>() {
            @Override
            public void onSuccess(RtcInfo result) {
                initAgoraManager(result);
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "onError: ===获取声网token失败" + err);
            }
        });

    }

    private void initAgoraManager(RtcInfo rtcInfo) {
        Log.d(TAG, "===initAgoraManager");
        mAgoraManager = new AgoraManager(this, mUserId, mRoomId, rtcInfo);
        mAgoraManager.setEventListener(this);

        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            mAgoraManager.setMoviePath(movieInfo.getMovieUrl());
        }

        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            setBroadcasterUI();
            mBinding.tvStatus.setText("准备媒体中...");
            mAgoraManager.prepareMediaPlayer();
        } else {
            setAudienceUI();
            mBinding.tvStatus.setText("准备加入...");
        }
        mAgoraManager.loginRtm(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                runOnUiThread(() -> {
                    if (roleType == RoleType.ROLE_TYPE_AUDIENCE) {
                        mBinding.tvStatus.setText("等待主播视频流...");
                        mAgoraManager.joinChannel(false);
                    } else {
                        mBinding.tvStatus.setText("媒体加载中...");
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
        mBinding.remoteVideoViewContainer.addView(localView);
        mAgoraManager.setupLocalVideo(localView);

        mBinding.btnPlayPause.setEnabled(true);

        mBinding.seekBar.setEnabled(true);
    }


    private void setAudienceUI() {

        mBinding.btnPlayPause.setEnabled(false);
        mBinding.seekBar.setEnabled(false);

        mBinding.tvStatus.setText("等待主播加入...");
    }

    private boolean checkSelfPermissions() {
        for (String permission : REQUESTED_PERMISSIONS) {
            if (mPermissionManager.isPermitPermission(permission)) {
                return false;
            }
        }
        return true;
    }


    private void initView() {
        remote_video_view_containerParams = mBinding.remoteVideoViewContainer.getLayoutParams();
        rl_controllersParams = mBinding.rlControllers.getLayoutParams();
        rl_video_containerParams = mBinding.rlVideoContainer.getLayoutParams();

        mBinding.remoteVideoViewContainer.setOnClickListener(this);
        mBinding.rlControllers.setVisibility(View.GONE);
        mBinding.btnPlayPause.setOnClickListener(this);
        mBinding.btnSend.setOnClickListener(this);
        mBinding.ivLandscape.setOnClickListener(this);
        mBinding.ivMenu.setOnClickListener(this);


        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mAgoraManager != null) {
                    mAgoraManager.seek((long) (progress * 1000));

                    if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
                        mAgoraManager.sendSyncCommand(progress, System.currentTimeMillis());
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

                mBinding.tvStatus.setText("已连接房主: " + uid);

                SurfaceView remoteView = new SurfaceView(this);
                mBinding.remoteVideoViewContainer.removeAllViews();
                mBinding.remoteVideoViewContainer.addView(remoteView);
                mAgoraManager.setupRemoteVideo(uid, remoteView);
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
                    runOnUiThread(() -> mBinding.tvStatus.setText("播放中"));
                    break;
                case "pause":
                    runOnUiThread(() -> mBinding.tvStatus.setText("已暂停"));
                    break;
                case "sync":
                    int progress = json.getInt("progress");
//                    long timestamp = json.getLong("timestamp");
                    runOnUiThread(() -> {
                        if (Math.abs(mBinding.seekBar.getProgress() - progress) > 2) {
                            mBinding.seekBar.setProgress(progress);
                        }
                    });
                    break;
                case "comment":
                    String sender = json.getString("sender");
                    String content = json.getString("content");
                    long timestamp = json.getLong("timestamp");
                    runOnUiThread(() -> {
                        if (!mUserId.equals(sender)) {
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
                mAgoraManager.joinChannel(true);
                mBinding.tvStatus.setText("等待观众加入...");
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
            if (Math.abs(mBinding.seekBar.getProgress() - progress) > 2) {
                mBinding.seekBar.setProgress(progress);
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
        if (mAgoraManager.isPlaying()) {
            mAgoraManager.pause();
            mAgoraManager.sendControlCommand("pause");
            mBinding.tvStatus.setText("已暂停");
        } else {
            mAgoraManager.play();
            mAgoraManager.sendControlCommand("play");
            mBinding.tvStatus.setText("播放中");
        }
    }

    private void sendComment() {
        String comment = mBinding.etComment.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            addComment(mUserId, comment, System.currentTimeMillis());
            mAgoraManager.sendComment(comment);
            mBinding.etComment.setText("");
        }
    }

    private void adjustControlBar() {
        if (mBinding.rlControllers.getVisibility() == View.GONE) {
            mBinding.rlControllers.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                if (mBinding.rlControllers.getVisibility() == View.VISIBLE) {
                    mBinding.rlControllers.setVisibility(View.GONE);
                }
            }, 3000);
        } else {
            mBinding.rlControllers.setVisibility(View.GONE);
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
        shareMsg.setLinkContent(mRoomId);

        shareMsg.setLinkImageUrl(movieInfo.getCoverUrl());
        shareMsg.setSenderEmail(SPManager.getUserInfo(this).getEmail());
        shareMsg.setReceiverEmail(friendInfo.getEmail());

        Intent intent = new Intent(AudioActivity.this, ChatActivity.class);
        intent.putExtra(AppProperties.CHAT_ID, friendInfo.getEmail());
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
            mBinding.rlPortraitLayout.setVisibility(View.GONE);
            mBinding.rlTitle.setVisibility(View.GONE);


            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mBinding.remoteVideoViewContainer.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams controllerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            controllerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mBinding.rlControllers.setLayoutParams(controllerParams);



        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "enterFullScreenMode: ===进入全屏模式错误" + e.getMessage());
        }
    }

    private void exitFullScreenMode() {
        try {
            Log.d(TAG, "exitFullScreenMode: ===退出全屏");
            isFullScreen = false;
            mBinding.rlPortraitLayout.setVisibility(View.VISIBLE);
            mBinding.rlTitle.setVisibility(View.VISIBLE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            mBinding.remoteVideoViewContainer.setLayoutParams(remote_video_view_containerParams);
            mBinding.rlControllers.setLayoutParams(rl_controllersParams);



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
        mAdapter.notifyDataSetChanged();
        mBinding.lvComment.smoothScrollToPosition(commentList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgoraManager != null) {
            mAgoraManager.leaveChannel();
            mAgoraManager.destroy();
        }
        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            MovieManager movieManager = new MovieManager();
            movieManager.releaseRoom(token, mRoomId);
        }
    }

}
package com.memory.wq.activities;

import android.content.Intent;
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
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityAudioBinding;
import com.memory.wq.enumertions.RoleType;
import com.memory.wq.interfaces.AgoraEventListener;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.AgoraManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.MovieManager;
import com.memory.wq.managers.PermissionManager;
import com.memory.wq.managers.TokenManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.ShareFunctionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.mediaplayer.Constants;

public class AudioActivity extends BaseActivity<ActivityAudioBinding> {
    private static final String TAG = "WQ_AudioActivity";

    private static final int PERMISSION_REQ_ID = 22;
    private final List<MovieCommentInfo> commentList = new ArrayList<>();
    private final List<FriendInfo> friendList = new ArrayList<>();

    private AgoraManager mAgoraManager;
    private RoleType roleType;
    private long mRoomId;
    private final MovieManager mMovieManager = new MovieManager();
    ;
    private PermissionManager mPermissionManager;
    private long mUserId;
    private MovieCommentAdapter mAdapter;
    private boolean isFullScreen = false;

    private ViewGroup.LayoutParams rl_controllersParams;
    private ViewGroup.LayoutParams remote_video_view_containerParams;
    private MovieInfo movieInfo;
    private final AgoraListenerImpl mAgoraListener = new AgoraListenerImpl();
    private final TokenManager mRtcTokenManager = new TokenManager();
    private final UserManager mUserManager = new UserManager();
    private final SeekBarChangeListenerImpl mSeekBarListener = new SeekBarChangeListenerImpl();

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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        //todo 直接退回桌面再清理应用后台不会触发
        if (movieInfo != null) {
            long currentMsPosition = mAgoraManager.getCurrentPosition();
            saveProgress(movieInfo.getMovieId(), (int) currentMsPosition/1000);
        }

        if (mAgoraManager != null) {
            mAgoraManager.leaveChannel();
            mAgoraManager.destroy();
        }
        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {

            mMovieManager.releaseRoom(String.valueOf(mRoomId));
        }

        super.onDestroy();
    }

    private void initCommentList() {
        mAdapter = new MovieCommentAdapter(this, commentList);
        mBinding.lvComment.setAdapter(mAdapter);
    }

    private void initPermissions() {
        mPermissionManager = new PermissionManager(this);
        initData();
    }

    private void initData() {

        mUserId = AccountManager.getUserId();

        Intent intent = getIntent();
        roleType = (RoleType) intent.getSerializableExtra(AppProperties.ROLE_TYPE);
        mRoomId = roleType == RoleType.ROLE_TYPE_BROADCASTER ? mUserId
                : intent.getLongExtra(AppProperties.ROOM_ID, -1L);

        if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
            movieInfo = (MovieInfo) intent.getSerializableExtra(AppProperties.MOVIE);
            mMovieManager.saveRoom(mUserId, movieInfo.getMovieId());
        }

        int role = roleType == RoleType.ROLE_TYPE_BROADCASTER ? 1 : 2;


        mRtcTokenManager.getToken(mUserId, mRoomId, role, new ResultCallback<RtcInfo>() {
            @Override
            public void onSuccess(RtcInfo result) {
                Log.d(TAG, "[test] initData #141" + result);
                initAgoraManager(result);
            }

            @Override
            public void onError(String err) {
                Log.d(TAG, "[x] getRtcToken #147" + err);
            }
        });

    }

    private void initAgoraManager(RtcInfo rtcInfo) {
        mAgoraManager = new AgoraManager(this, mUserId, mRoomId, rtcInfo);
        mAgoraManager.setEventListener(mAgoraListener);

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
                //不可删除主线程运行!!!!!!!!!!!!!!!!!!!!!
                runOnUiThread(()->{
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
                Log.d(TAG, "[x] loginRtm #160" + err);
            }
        });
    }

    private void setBroadcasterUI() {
        Log.d(TAG, "setBroadcasterUI: ===设置主播端ui");
        SurfaceView localView = new SurfaceView(this);
        localView.setZOrderMediaOverlay(true);
        mBinding.remoteVideoViewContainer.addView(localView);
        mAgoraManager.setupLocalVideo(localView);

        mBinding.btnPlayPause.setVisibility(View.VISIBLE);

        mBinding.seekBar.setVisibility(View.VISIBLE);
    }


    private void setAudienceUI() {
        mBinding.btnPlayPause.setVisibility(View.GONE);
        mBinding.seekBar.setVisibility(View.GONE);
        mBinding.tvStatus.setText("等待主播加入...");
    }

    private void initView() {
        remote_video_view_containerParams = mBinding.remoteVideoViewContainer.getLayoutParams();
        rl_controllersParams = mBinding.rlControllers.getLayoutParams();
        mBinding.rlControllers.setVisibility(View.GONE);

        mBinding.remoteVideoViewContainer.setOnClickListener(V -> {
            adjustControlBar();
        });

        mBinding.btnPlayPause.setOnClickListener(V -> {
            switchPlayPause();
        });
        mBinding.btnSend.setOnClickListener(V -> {
            sendComment();
        });
        mBinding.ivLandscape.setOnClickListener(V -> {
            switchOrientation();
        });


        mBinding.seekBar.setOnSeekBarChangeListener(mSeekBarListener);
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

    private void switchPlayPause() {
        if (mAgoraManager.isPlaying()) {
            mAgoraManager.pause();
            mAgoraManager.sendControlCommand("pause");
            mBinding.tvStatus.setText("已暂停");
            mBinding.btnPlayPause.setImageResource(R.mipmap.icon_play);
        } else {
            mAgoraManager.play();
            mAgoraManager.sendControlCommand("play");
            mBinding.tvStatus.setText("播放中");
            mBinding.btnPlayPause.setImageResource(R.mipmap.icon_pause);
        }
    }

    private void sendComment() {
        String comment = mBinding.etComment.getText().toString();
        if (!TextUtils.isEmpty(comment)) {
            addComment(String.valueOf(mUserId), comment, System.currentTimeMillis());
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
        new FriendManager().getFriends(new ResultCallback<List<FriendInfo>>() {
            @Override
            public void onSuccess(List<FriendInfo> result) {
                MyToast.showToast(AudioActivity.this, "缺少逻辑");
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
//        shareMsg.setMsgType(ContentType.TYPE_LINK);
//        shareMsg.setLinkTitle("加入共享房间");
//        shareMsg.setContent("点击链接加入观看" + movieInfo.getTitle());
//        shareMsg.setLinkContent(String.valueOf(mRoomId));
//
//        shareMsg.setLinkImageUrl(movieInfo.getCoverUrl());
//        shareMsg.setSenderId(SPManager.getUserInfo().getUuNumber());
//        shareMsg.setReceiverId(friendInfo.getUuNumber());

        Intent intent = new Intent(AudioActivity.this, ChatActivity.class);
        intent.putExtra(AppProperties.CHAT_ID, friendInfo.getUuNumber());
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


            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mBinding.remoteVideoViewContainer.setLayoutParams(videoParams);

            RelativeLayout.LayoutParams controllerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            mBinding.ivLandscape.setImageResource(R.mipmap.icon_landscape);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mBinding.ivLandscape.setImageResource(R.mipmap.icon_portrait);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void addComment(String userId, String comment, long timestamp) {

        mUserManager.getUserById(Long.parseLong(userId), new ResultCallback<FriendInfo>() {

            @Override
            public void onSuccess(FriendInfo result) {
                MovieCommentInfo movieCommentInfo = new MovieCommentInfo();
                movieCommentInfo.setContent(comment);
                movieCommentInfo.setSender(result.getNickname());
                movieCommentInfo.setTimestamp(timestamp);
                commentList.add(movieCommentInfo);
                mAdapter.notifyDataSetChanged();
                mBinding.lvComment.smoothScrollToPosition(commentList.size() - 1);
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    private void saveProgress(int movieId, int currentSecondPosition) {
        Log.d(TAG, "[test] saveProgress "+currentSecondPosition);
        mMovieManager.saveWatchProgress(movieId, currentSecondPosition);
    }


    private class AgoraListenerImpl implements AgoraEventListener {

        @Override
        public void onRemoteUserJoined(int uid) {
            runOnUiThread(() -> {
                if (roleType == RoleType.ROLE_TYPE_AUDIENCE) {
                    mBinding.tvStatus.setText("");
                    SurfaceView remoteView = new SurfaceView(AudioActivity.this);
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
                        runOnUiThread(() -> {
                            mBinding.tvStatus.setText("播放中");
                            mBinding.btnPlayPause.setImageResource(R.mipmap.icon_pause);
                        });
                        break;
                    case "pause":
                        runOnUiThread(() -> {
                            mBinding.tvStatus.setText("已暂停");
                            mBinding.btnPlayPause.setImageResource(R.mipmap.icon_play);
                        });
                        break;
                    case "sync":
                        long progress = json.getLong("progress");
                        long syncTimestamp = json.getLong("timestamp");
                        Log.d(TAG, "[test] AgoraListenerImpl #492" + progress + "===" + syncTimestamp);
                        runOnUiThread(() -> {
                            if (Math.abs(mBinding.seekBar.getProgress() - progress) > 2) {
                                mBinding.seekBar.setProgress((int) progress);
                            }
                        });
                        break;
                    case "comment":
                        String sender = json.getString("sender");
                        String content = json.getString("content");
                        long msgTimestamp = json.getLong("timestamp");
                        runOnUiThread(() -> {
                            if (!String.valueOf(mUserId).equals(sender)) {
                                addComment(sender, content, msgTimestamp);
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
            if (roleType != RoleType.ROLE_TYPE_BROADCASTER) {
                Log.d(TAG, "[x] onMediaStateChanged #507");
                return;
            }

            if (state == Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
                runOnUiThread(() -> {
                    setBroadcasterUI();
                    mAgoraManager.joinChannel(true);
                    mBinding.tvStatus.setText("");

                    long durationMs = mAgoraManager.getDuration();
                    if (durationMs > 0) {
                        int durationSeconds = (int) (durationMs / 1000); // 转换为秒
                        mBinding.seekBar.setMax(durationSeconds); // 设置最大值
                        Log.d(TAG, "设置 SeekBar 最大值为: " + durationSeconds + " 秒");
                    }
                });

            }
        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            // 首次获取到视频尺寸时计算宽高比
            Log.d(TAG, "onFirstRemoteVideoFrame: ===视频chicun:" + width + "==高:" + height);
        }

        @Override
        public void onPlaybackProgress(int progressInSeconds) {
            if (Math.abs(mBinding.seekBar.getProgress() - progressInSeconds) > 1) {
                mBinding.seekBar.setProgress(progressInSeconds);
            }
        }
    }

    private class SeekBarChangeListenerImpl implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int seekBarProgress = seekBar.getProgress(); // 这是 SeekBar 的进度 (0-max)
            int seekBarMax = seekBar.getMax();

            if (mAgoraManager == null) {
                Log.d(TAG, "[x] onStopTrackingTouch #231");
                return;
            }

            long duration = mAgoraManager.getDuration();
            if (duration <= 0) {
                Log.d(TAG, "[x] onStopTrackingTouch #238");
                return;
            }

            long targetPosition = (duration * seekBarProgress) / seekBarMax;
            mAgoraManager.seek(targetPosition);

            if (roleType == RoleType.ROLE_TYPE_BROADCASTER) {
                // 发送实际的毫秒时间，而不是 SeekBar 的进度值
                mAgoraManager.sendSyncCommand((int) (targetPosition / 1000), System.currentTimeMillis());
            }
        }
    }

}
package com.memory.wq.managers;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import com.memory.wq.beans.RtcInfo;
import com.memory.wq.properties.AppProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.agora.mediaplayer.IMediaPlayer;
import io.agora.mediaplayer.IMediaPlayerObserver;
import io.agora.mediaplayer.data.CacheStatistics;
import io.agora.mediaplayer.data.PlayerPlaybackStats;
import io.agora.mediaplayer.data.PlayerUpdatedInfo;
import io.agora.mediaplayer.data.SrcInfo;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.LinkStateEvent;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PublishOptions;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmEventListener;
import io.agora.rtm.SubscribeOptions;

public class AgoraManager {
    public static final String TAG = "WQ_AgoraManager";

    private String userId;
    private String channelName;
    private String appId;
    private String moviePath;
    private boolean isBroadcaster;

    private RtcEngine mRtcEngine;
    private boolean isMediaOpened = false;
    private IMediaPlayer mediaPlayer;

    private RtmClient mRtmClient;

    private Context context;
    private String rtcToken;

    private AgoraEventListener eventListener;


    public AgoraManager(Context context, String userId, String channelName, RtcInfo rtcInfo) {
        this.context = context;
        this.userId = userId;
        this.channelName = channelName;

        this.appId = rtcInfo.getAppId();
        this.rtcToken = rtcInfo.getToken();

        initRtcEngine();
        initRtmClient();
    }


    private void initRtcEngine() {
        Log.d(TAG, "===initRtcEngine");
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = context;
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

            mRtcEngine = RtcEngine.create(config);

            VideoEncoderConfiguration videoConfig = new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            );

            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(videoConfig);

        } catch (Exception e) {
            Log.d(TAG, "initRtcEngine: ===rtc初始化异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initRtmClient() {
        Log.d(TAG, "===initRtcEngine");
        RtmConfig rtmConfig = new RtmConfig.Builder(appId, userId)
                .eventListener(rtmEventListener)
                .build();

        try {
            mRtmClient = RtmClient.create(rtmConfig);
        } catch (Exception e) {
            Log.d(TAG, "initRtmClient:=== RTM初始化异常:" + e.getMessage());
        }
    }

    public void setEventListener(AgoraEventListener listener) {
        this.eventListener = listener;
    }

    public void setMoviePath(String moviePath) {
        this.moviePath = moviePath;
    }

    public void prepareMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = mRtcEngine.createMediaPlayer();
            mediaPlayer.setRenderMode(Constants.RENDER_MODE_FIT);
            mediaPlayer.registerPlayerObserver(mediaPlayerObserver);
            playMedia(moviePath);
        }
    }

    public void playMedia(String path) {
        if (mediaPlayer != null) {
            int code = mediaPlayer.open(path, 0);
            Log.d(TAG, "playMedia: ===电影地址" + AppProperties.HTTP_SERVER_ADDRESS + path);
            Log.d(TAG, "playMedia: ===播放码" + code);
            Log.d(TAG, "MediaPlayer state after open: " + mediaPlayer.getState());
        }
    }

    public void loginRtm(com.memory.wq.utils.ResultCallback<Boolean> callback) {
        mRtmClient.login(rtcToken, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.d(TAG, "onSuccess: ===rtm登陆成功");
                subscribeRtmChannel(callback);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.d(TAG, "onFailure: ===rtm登录失败" + errorInfo.toString());
            }
        });
    }

    private void subscribeRtmChannel(com.memory.wq.utils.ResultCallback<Boolean> callback) {
        SubscribeOptions options = new SubscribeOptions();
        options.setWithMessage(true);
        mRtmClient.subscribe(channelName, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.d(TAG, "onSuccess: ===订阅rtm频道成功");
                callback.onSuccess(true);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.d(TAG, "onFailure: ===订阅rtm频道失败");
            }
        });
    }

    public void joinChannel(boolean isBroadcaster) {
        Log.d(TAG, "joinChannel: ===加入频道" + isBroadcaster);
        this.isBroadcaster = isBroadcaster;

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        options.clientRoleType = isBroadcaster ? Constants.CLIENT_ROLE_BROADCASTER : Constants.CLIENT_ROLE_AUDIENCE;

        options.autoSubscribeAudio = true;
        options.autoSubscribeVideo = true;

        if (isBroadcaster && mediaPlayer != null && isMediaOpened) {
            Log.d(TAG, "joinChannel: ===主播端加入 频道");
            options.publishMediaPlayerId = mediaPlayer.getMediaPlayerId();
            options.publishMediaPlayerAudioTrack = true;
            options.publishMediaPlayerVideoTrack = true;
            options.publishCameraTrack = false;
            options.publishMicrophoneTrack = false;
            Log.d(TAG, "joinChannel: ===发布id" + mediaPlayer.getMediaPlayerId());
        } else {
            Log.d(TAG, "joinChannel: ===观众端加入频道");
            options.publishMediaPlayerVideoTrack = false;
            options.publishMediaPlayerAudioTrack = false;
            options.publishCameraTrack = false;
            options.publishMicrophoneTrack = false;
        }
        int code = mRtcEngine.joinChannelWithUserAccount(rtcToken, channelName, userId, options);
        Log.d(TAG, "joinChannel: ===加入rtc频道返回码" + code);
        Log.d(TAG, "===发布媒体ID: " + options.publishMediaPlayerId);
        Log.d(TAG, "===媒体状态: " + isMediaOpened);
    }

    public void setupLocalVideo(SurfaceView surfaceView) {
        if (mediaPlayer != null) {
            Log.d(TAG, "setupLocalVideo: ===设置本地视图");
            mediaPlayer.setView(surfaceView);
            mediaPlayer.setRenderMode(Constants.RENDER_MODE_FIT);
        }
    }

    public void setupRemoteVideo(int uid, SurfaceView surfaceView) {
        Log.d(TAG, "setupRemoteVideo: ===设置远端试图");
        VideoCanvas canvas = new VideoCanvas(surfaceView, Constants.RENDER_MODE_FIT, uid);
        mRtcEngine.setupRemoteVideo(canvas);
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void seek(long position) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(position);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getState() == io.agora.mediaplayer.Constants.MediaPlayerState.PLAYER_STATE_PLAYING;
    }

    public void sendControlCommand(String command) {
        sendRtmCommand("cmd", command);
    }

    public void sendSyncCommand(int progress, long timestamp) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "sync");
            json.put("progress", progress);
            json.put("timestamp", timestamp);
            publishRtmMessage(json.toString());
        } catch (JSONException e) {
            Log.d(TAG, "sendSyncCommand: ===同步命令出异常了" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendComment(String comment) {
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "comment");
            json.put("content", comment);
            json.put("sender", userId);
            json.put("timestamp", System.currentTimeMillis());
            publishRtmMessage(json.toString());
        } catch (JSONException e) {
            Log.d(TAG, "sendComment: ===评论消息异常" + e.getMessage());
        }
    }


    private void sendRtmCommand(String key, String value) {
        JSONObject json = new JSONObject();
        try {
            json.put(key, value);
            json.put("timestamp", System.currentTimeMillis());
            publishRtmMessage(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "sendRtmCommand: ===创建RTM命令失败");
        }
    }

    private void publishRtmMessage(String message) {
        if (mRtmClient == null)
            return;
        PublishOptions options = new PublishOptions();
        options.setCustomType("");

        mRtmClient.publish(channelName, message, options, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.d(TAG, "onSuccess: ===rtm消息发送成功" + message);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.d(TAG, "onFailure: ===rtm消息发送失败" + errorInfo.getErrorCode());
            }
        });
    }

    public void leaveChannel() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.destroy();
            mediaPlayer = null;
        }

        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }

        if (mRtmClient != null) {
            mRtmClient.unsubscribe(channelName, null);
            mRtmClient.logout(null);
        }
    }

    public void destroy() {

        if (mRtcEngine != null) {
            RtcEngine.destroy();
            mRtcEngine = null;
        }


        if (mRtmClient != null) {
            mRtmClient.release();
            mRtmClient = null;
        }
    }


    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d(TAG, "onJoinChannelSuccess: ===用户加入频道" + uid + "频道:" + channel);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.d(TAG, "onUserJoined: ===远程用户加入:" + uid);
            if (eventListener != null) {
                eventListener.onRemoteUserJoined(uid);
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.d(TAG, "onUserOffline:=== 用户离开:" + uid);
        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            if (eventListener != null) {
                Log.d(TAG, "onFirstRemoteVideoFrame: ====视频帧尺寸:" + width + "*" + height);
                eventListener.onFirstRemoteVideoFrame(uid, width, height, elapsed);
            }
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            Log.d(TAG, "onError: ===rtc事件错误" + err);
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            Log.d(TAG, "onConnectionStateChanged: ===连接状态改变" + state + "原因" + reason);
        }
    };

    private RtmEventListener rtmEventListener = new RtmEventListener() {
        @Override
        public void onLinkStateEvent(LinkStateEvent event) {
            Log.d(TAG, "onLinkStateEvent: ===rtm连接状态改变" + event.getCurrentState());
        }

        @Override
        public void onMessageEvent(MessageEvent event) {
            try {
                io.agora.rtm.RtmMessage rtmMessage = event.getMessage();
                String messageText = (String) rtmMessage.getData();

                if (messageText == null) {
                    byte[] rawData = (byte[]) rtmMessage.getData();
                    messageText = new String(rawData, StandardCharsets.UTF_8);
                }

                Log.d(TAG, "onMessageEvent:=== rtm新消息" + messageText);

                if (eventListener != null) {
                    eventListener.onRtmMessageReceived(messageText);
                }
            } catch (Exception e) {
                Log.d(TAG, "onMessageEvent:=== RTM消息异常" + e.getMessage());
                e.printStackTrace();
            }
//
//            try {
//                String messageText;
//                Object data = event.getMessage().getData();
//
//                if (data instanceof String) {
//                    messageText = (String) data;
//                } else if (data instanceof byte[]) {
//                    messageText = new String((byte[]) data, StandardCharsets.UTF_8);
//                } else {
//                    Log.w(TAG, "未知的RTM消息类型");
//                    return;
//                }
//
//                Log.d(TAG, "收到RTM消息: " + messageText);
//
//                if (eventListener != null) {
//                    eventListener.onRtmMessageReceived(messageText);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "处理RTM消息异常: " + e.getMessage());
//            }
        }
    };

    private final IMediaPlayerObserver mediaPlayerObserver = new IMediaPlayerObserver() {
        @Override
        public void onPlayerStateChanged(io.agora.mediaplayer.Constants.MediaPlayerState state, io.agora.mediaplayer.Constants.MediaPlayerReason reason) {
            if (eventListener != null) {
                eventListener.onMediaStateChanged(state);
                Log.d(TAG, "onPlayerStateChanged: ===播放器状态改变");
            }

            if (state == io.agora.mediaplayer.Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED) {
                isMediaOpened = true;
                play();
            }

        }

        @Override
        public void onPositionChanged(long positionMs, long timestampMs) {

            if (eventListener != null && mediaPlayer != null) {
                long duration = mediaPlayer.getDuration();
                if (duration > 0) {
                    int progress = (int) ((positionMs * 100) / duration);
                    eventListener.onPlaybackProgress(progress);
                }
            }
        }

        @Override
        public void onPlayerEvent(io.agora.mediaplayer.Constants.MediaPlayerEvent eventCode, long elapsedTime, String message) {

        }

        @Override
        public void onMetaData(io.agora.mediaplayer.Constants.MediaPlayerMetadataType type, byte[] data) {

        }

        @Override
        public void onPlayBufferUpdated(long playCachedBuffer) {

        }

        @Override
        public void onPreloadEvent(String src, io.agora.mediaplayer.Constants.MediaPlayerPreloadEvent event) {

        }

        @Override
        public void onAgoraCDNTokenWillExpire() {

        }

        @Override
        public void onPlayerSrcInfoChanged(SrcInfo from, SrcInfo to) {

        }

        @Override
        public void onPlayerInfoUpdated(PlayerUpdatedInfo info) {

        }

        @Override
        public void onPlayerCacheStats(CacheStatistics stats) {

        }

        @Override
        public void onPlayerPlaybackStats(PlayerPlaybackStats stats) {

        }

        @Override
        public void onAudioVolumeIndication(int volume) {

        }
    };


    public interface AgoraEventListener {
        void onRemoteUserJoined(int uid);

        void onRtmMessageReceived(String message);

        void onMediaStateChanged(io.agora.mediaplayer.Constants.MediaPlayerState state);

        void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed);

        void onPlaybackProgress(int progress);
    }


}

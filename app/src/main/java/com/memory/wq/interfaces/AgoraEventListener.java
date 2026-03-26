package com.memory.wq.interfaces;

public interface AgoraEventListener {
    void onRemoteUserJoined(int uid);

    void onRtmMessageReceived(String message);

    void onMediaStateChanged(io.agora.mediaplayer.Constants.MediaPlayerState state);

    void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed);

    void onPlaybackProgress(int progressInSeconds);
}

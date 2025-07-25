package com.memory.wq.provider;

import io.agora.mediaplayer.Constants.MediaPlayerState;

public interface AgoraEventListener {
    void onRemoteUserJoined(int uid);
    void onRtmMessageReceived(String message);
    void onMediaStateChanged(MediaPlayerState state);
}

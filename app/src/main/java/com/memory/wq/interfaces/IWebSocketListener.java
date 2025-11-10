package com.memory.wq.interfaces;

import com.memory.wq.enumertions.EventType;
import com.memory.wq.service.WebSocketMessage;

import java.util.EnumSet;

public interface IWebSocketListener {
    EnumSet<EventType> getEvents();

    <T> void onMessage(WebSocketMessage<T> message);

    void onConnectionChanged(boolean isConnected);
}

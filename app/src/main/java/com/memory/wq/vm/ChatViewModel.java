package com.memory.wq.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UiMessageState;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.repository.MessageRepository;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "WQ_ChatVM";
    private MessageRepository mMsgRepository = MessageRepository.getInstance();
    private final MutableLiveData<String> _chatId = new MutableLiveData<>();
    private final MutableLiveData<UiChatInfo> _uiChatInfo = new MutableLiveData<>();
    public LiveData<UiChatInfo> uiChatInfo = _uiChatInfo;
    public LiveData<String> chatId = _chatId;

    private final MutableLiveData<UiMessageState> _uiMessageState = new MutableLiveData<>();
    public final LiveData<UiMessageState> uiMessageState = _uiMessageState;
    private MsgManager msgManager = new MsgManager();
    private LiveData<WebSocketMessage<?>> webSocketMessages;


    public ChatViewModel() {
        subscribeToWebSocketMessages();
    }

    private void subscribeToWebSocketMessages() {
        // 观察 WebSocket 消息并在内部处理
        MessageRepository.getInstance().getMessages().observeForever(webSocketMessage -> {
            if (webSocketMessage != null && webSocketMessage.getEventType() == EventType.EVENT_TYPE_MSG) {
                List<MsgInfo> newMsgList = (List<MsgInfo>) webSocketMessage.getData();
                if (newMsgList != null && !newMsgList.isEmpty()) {
                    handleNewMessages(newMsgList);
                }
            }
        });
    }

    public void setChatId(String chatId) {
        if (_chatId.getValue() == null || !_chatId.getValue().equals(chatId)) {
            _chatId.setValue(chatId);
            loadChatInfo(chatId);
            loadMessages();
        }
    }

    private void loadChatInfo(String chatId) {

    }

    public void loadMessages() {
        if (msgManager == null) {
            Log.d(TAG, "[x] loadMessages #46");
            return;
        }

        _uiMessageState.setValue(new UiMessageState.Loading());


        msgManager.getMsg(_chatId.getValue(), new ResultCallback<List<MsgInfo>>() {
            @Override
            public void onSuccess(List<MsgInfo> result) {
                _uiMessageState.postValue(new UiMessageState.DisPlay(result));
            }

            @Override
            public void onError(String err) {
                _uiMessageState.postValue(new UiMessageState.Error(err));
            }
        });
    }

    public void handleNewMessages(List<MsgInfo> newMessages) {
        // 获取当前状态并合并新消息
        UiMessageState currentState = _uiMessageState.getValue();
        if (currentState instanceof UiMessageState.DisPlay) {
            List<MsgInfo> currentMessages = ((UiMessageState.DisPlay) currentState).getMsgInfoList();
            // 合并当前消息和新消息的逻辑
            // 这里简单地将新消息添加到现有消息列表中
            // 实际应用中可能需要更复杂的合并逻辑
            currentMessages.addAll(newMessages);
            _uiMessageState.postValue(new UiMessageState.DisPlay(currentMessages));
        } else {
            // 如果还没有加载历史消息，直接显示新消息
            _uiMessageState.postValue(new UiMessageState.DisPlay(newMessages));
        }
    }

}

package com.memory.wq.vm;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UiMessageState;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.provider.MsgProvider;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.repository.MsgRepository;
import com.memory.wq.repository.WSRepository;
import com.memory.wq.service.WebSocketMessage;
import com.memory.wq.utils.ResultCallback;

import java.util.List;
import java.util.function.Consumer;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "WQ_ChatVM";
    private final MsgRepository mRepository = new MsgRepository();
    private ContentObserver observer;
    private final MutableLiveData<String> _chatId = new MutableLiveData<>();
    private final MutableLiveData<UiChatInfo> _uiChatInfo = new MutableLiveData<>();
    public LiveData<UiChatInfo> uiChatInfo = _uiChatInfo;
    public LiveData<String> chatId = _chatId;

    private final MutableLiveData<UiMessageState> _uiMessageState = new MutableLiveData<>();
    public final LiveData<UiMessageState> uiMessageState = _uiMessageState;
    private MsgManager mMsgManager = new MsgManager();
    private final String curUser = AccountManager.getUserInfo(null).getEmail();

    public void setChatId(String chatId) {
        if (_chatId.getValue() == null || !_chatId.getValue().equals(chatId)) {
            _chatId.setValue(chatId);
            loadChatInfo(chatId);
            registerDBObserver();
            loadMessages();
        }
    }

    private void registerDBObserver() {
        ContentResolver resolver = WqApplication.getInstance().getContentResolver();
        if (observer != null) {
            resolver.unregisterContentObserver(observer);
        }

        observer = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                reloadMessagesFromDB();
            }
        };

        resolver.registerContentObserver(
                MsgProvider.CONTENT_URI,
                true,
                observer
        );
    }

    private void reloadMessagesFromDB() {
        mRepository.loadMessages(curUser, _chatId.getValue(), new ResultCallback<List<MsgInfo>>() {
            @Override
            public void onSuccess(List<MsgInfo> result) {
                _uiMessageState.postValue(new UiMessageState.DisPlay(result));
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    private void loadChatInfo(String chatId) {

    }

    public void loadMessages() {
        if (mMsgManager == null) {
            Log.d(TAG, "[x] loadMessages #46");
            return;
        }

        _uiMessageState.setValue(new UiMessageState.Loading());

        mRepository.loadMessages(curUser, _chatId.getValue(), new ResultCallback<List<MsgInfo>>() {
            @Override
            public void onSuccess(List<MsgInfo> result) {
                _uiMessageState.postValue(new UiMessageState.DisPlay(result));
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    public void sendMsg(String token, String msg, Consumer<Boolean> callback) {
        mMsgManager.sendMsg(token, chatId.getValue(), msg, new ResultCallback<List<MsgInfo>>() {

            @Override
            public void onSuccess(List<MsgInfo> result) {
                callback.accept(true);
            }

            @Override
            public void onError(String err) {
                callback.accept(false);
            }
        });
    }

    public void deleteMsg(int msgId, Consumer<Boolean> callback){
        mMsgManager.deleteMsg(msgId, new ResultCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onError(String err) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (observer == null) {
            return;
        }

        WqApplication.getInstance().getContentResolver().unregisterContentObserver(observer);
    }
}


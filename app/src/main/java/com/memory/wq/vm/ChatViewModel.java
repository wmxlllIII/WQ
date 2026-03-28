package com.memory.wq.vm;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.memory.wq.beans.FriendInfo;
import com.memory.wq.beans.MsgInfo;
import com.memory.wq.beans.UiChatInfo;
import com.memory.wq.beans.UiMessageState;
import com.memory.wq.enumertions.ChatPage;
import com.memory.wq.enumertions.ChatType;
import com.memory.wq.enumertions.ContentType;
import com.memory.wq.fragment.MessageFragment;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.MsgManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.provider.MsgProvider;
import com.memory.wq.provider.WqApplication;
import com.memory.wq.repository.MsgRepository;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "WQ_ChatVM";
    private final MsgRepository mRepository = new MsgRepository();
    private ContentObserver observer;
    private final MutableLiveData<Long> _chatId = new MutableLiveData<>(-1L);
    public LiveData<Long> chatId = _chatId;

    private final MutableLiveData<Integer> _chatType = new MutableLiveData<>(-1);
    public LiveData<Integer> chatType = _chatType;

    private final MutableLiveData<UiChatInfo> _uiChatInfo = new MutableLiveData<>();
    public LiveData<UiChatInfo> uiChatInfo = _uiChatInfo;

    private final MutableLiveData<UiMessageState> _uiMessageState = new MutableLiveData<>();
    public final LiveData<UiMessageState> uiMessageState = _uiMessageState;

    private final MutableLiveData<ChatPage> _chatPage = new MutableLiveData<>(ChatPage.CHAT_INDIVIDUAL);
    public LiveData<ChatPage> chatPage = _chatPage;

    private final MsgManager mMsgManager = new MsgManager();
    private final UserManager mUserManager = new UserManager();
    private final long curUser = AccountManager.getUserInfo().getUuNumber();

    public void setChatId(long chatId, int chatType) {
        _chatId.setValue(chatId);
        _chatType.setValue(chatType);
        loadChatInfo();
        registerDBObserver();
        loadMessages();
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
        if (_chatId.getValue() == null || _chatId.getValue() <= 0) {
            Log.d(TAG, "[x] reloadMessagesFromDB #82");
            return;
        }

        if (_chatType.getValue() == null || _chatType.getValue() < 0) {
            Log.d(TAG, "[x] reloadMessagesFromDB #86");
            return;
        }

        mRepository.loadMessages(_chatId.getValue(), ChatType.fromInt(_chatType.getValue()), new ResultCallback<List<MsgInfo>>() {
            @Override
            public void onSuccess(List<MsgInfo> result) {
                _uiMessageState.postValue(new UiMessageState.DisPlay(result));
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    private void loadChatInfo() {
        mUserManager.getChatInfoById(_chatId.getValue(), _chatType.getValue(), new ResultCallback<UiChatInfo>() {
            @Override
            public void onSuccess(UiChatInfo chatInfo) {
                if (chatInfo == null) {
                    Log.d(TAG, "[x] loadChatInfo #115");
                    return;
                }

                _uiChatInfo.postValue(chatInfo);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    public void loadMessages() {
        _uiMessageState.setValue(new UiMessageState.Loading());
        if (_chatId.getValue() == null || _chatId.getValue() <= 0) {
            _uiMessageState.setValue(new UiMessageState.Error("聊天对象错误"));
            Log.d(TAG, "[x] reloadMessagesFromDB #82");
            return;
        }

        if (_chatType.getValue() == null || _chatType.getValue() < 0) {
            _uiMessageState.setValue(new UiMessageState.Error("聊天类型错误"));
            Log.d(TAG, "[x] reloadMessagesFromDB #86");
            return;
        }

        mRepository.loadMessages(_chatId.getValue(), ChatType.fromInt(_chatType.getValue()), new ResultCallback<List<MsgInfo>>() {
            @Override
            public void onSuccess(List<MsgInfo> result) {
                _uiMessageState.postValue(new UiMessageState.DisPlay(result));
            }

            @Override
            public void onError(String err) {

            }
        });

    }

    public void sendMsg(String msg, ContentType msgType, Consumer<Boolean> callback) {
        mMsgManager.sendMsg(_chatId.getValue(), ChatType.fromInt(_chatType.getValue()), msg, msgType, new ResultCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                callback.accept(true);
            }

            @Override
            public void onError(String err) {
                callback.accept(false);
            }
        });
    }

    public void deleteMsg(int msgId, Consumer<Boolean> callback) {
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

    public void navigateTo(ChatPage page) {
        _chatPage.setValue(page);
    }

    public void navigateBack() {

    }

    public void buildGroup(String groupName, Set<Long> selectedUsers, ResultCallback<Boolean> callback) {
        if (selectedUsers == null || selectedUsers.isEmpty() || TextUtils.isEmpty(groupName) ) {
            Log.d(TAG, "[x] buildGroupOrChat: #159");
            return;
        }

        if (selectedUsers.size() < 3) {
            Log.d(TAG, "[x] buildGroupOrChat: #164");
            return;
        }

        mMsgManager.buildGroup(groupName, selectedUsers, callback);
    }
}


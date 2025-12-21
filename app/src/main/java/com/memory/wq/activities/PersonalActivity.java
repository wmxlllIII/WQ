package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.memory.wq.R;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.ActivityPersonalBinding;
import com.memory.wq.dialog.AddFriendDialog;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.AccountManager;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.MyToast;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonalActivity extends BaseActivity<ActivityPersonalBinding> {

    private static final String TAG = "WQ_PersonalActivity";
    private final FriendManager mFriendManager = new FriendManager();
    private long mFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mFriendId = (long) intent.getSerializableExtra(AppProperties.PERSON_ID);
        if (mFriendId <= 0) {
            Log.d(TAG, "[x] loadPersonalInfo #58");
            return;
        }
        initView();
        initData();
    }

    private void initView() {
        mBinding.ivBack.setOnClickListener(view -> finish());

        mBinding.ivAdd.setOnClickListener(view -> {
            AddFriendDialog dialog = new AddFriendDialog(mBinding.getRoot().getContext());
            dialog.setContent("我是" + AccountManager.getUserInfo().getUserName());
            dialog.setOnConfirmListener(new AddFriendDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String content) {
                    sendReq(mFriendId, content);
                }

                @Override
                public void onCancel() {
                }
            });
            dialog.show();
        });

        mBinding.ivChat.setOnClickListener(v -> {
            Intent intent = new Intent(mBinding.getRoot().getContext(), ChatActivity.class);
            intent.putExtra(AppProperties.CHAT_ID, mFriendId);
            startActivity(intent);
        });
        loadPersonalInfo();
    }

    private void loadPersonalInfo() {
        mFriendManager.searchUser(SearchUserType.SEARCH_USER_TYPE_UUNUM, String.valueOf(mFriendId), new ResultCallback<FriendInfo>() {
            @Override
            public void onSuccess(FriendInfo friend) {
                mBinding.tvNickname.setText(friend.getNickname());
                mBinding.tvUunum.setText(String.valueOf(mFriendId));
                mBinding.tvSignature.setText("todo");
//                mBinding.ivAdd.setVisibility();
//                mBinding.ivChat.setVisibility();
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    private void initData() {

    }

    private void sendReq(long targetId, String validMsg) {
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getApplyFriendJson(targetId, validMsg);
            HttpStreamOP.postJson(AppProperties.FRIEND_REQ, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d(TAG, "[x] sendReq 申请失败 #112");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "[x] sendReq 申请失败 #118" + response.code());
                    }
                    String body = response.body().string();
                    Log.d(TAG, " sendReq #123: " + body);
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        int code = jsonObject.getInt("code");
                        if (code == 1) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String state = data.getString("state");
                            if (state.equals("已申请")) {
                                Log.d(TAG, "[✓] sendReq #123");
                                runOnUiThread(() -> {
                                    MyToast.showToast(PersonalActivity.this, "发送成功");
                                });
                            } else if (state.equals("已申请")) {
                                MyToast.showToast(PersonalActivity.this, "发送成功");
                                runOnUiThread(() -> {

                                });
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }
}
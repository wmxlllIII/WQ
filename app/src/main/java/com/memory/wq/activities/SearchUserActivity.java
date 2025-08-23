package com.memory.wq.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.adapters.SearchFriendAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.SearchUserLayoutBinding;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.thread.ThreadPoolManager;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchUserActivity extends BaseActivity<SearchUserLayoutBinding> {

    private static final String TAG = SearchUserActivity.class.getName();
    private FriendManager mFriendManager;
    private SharedPreferences sp;
    private List<FriendInfo> mFriendList;
    private SearchFriendAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_user_layout;
    }


    private void initData() {
        mFriendManager = new FriendManager();
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);

        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setNickname("Test");
        mFriendList = new ArrayList<>();
        mFriendList.add(friendInfo);


        mAdapter = new SearchFriendAdapter(this, mFriendList);
        mBinding.lvSearch.setAdapter(mAdapter);

    }

    private void initView() {
        mBinding.tvTest.setOnClickListener(view -> {
            String account = mBinding.etAccount.getText().toString().trim();
            String token = sp.getString("token", "");
            mFriendManager.searchUser(SearchUserType.SEARCH_USER_TYPE_EMAIL, account, token, new ResultCallback<FriendInfo>() {
                @Override
                public void onSuccess(FriendInfo result) {
                    runOnUiThread(() -> {
                        mFriendList.clear();
                        mFriendList.add(result);
                        mAdapter.notifyDataSetChanged();

                    });

                    System.out.println("=================YES " + result.toString());
                }

                @Override
                public void onError(String err) {

                }
            });
        });

        mBinding.lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = (FriendInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchUserActivity.this, PersonalActivity.class);
                intent.putExtra(AppProperties.FRIENDINFO, friendInfo);
                startActivity(intent);
//                sendReq(friendInfo.getEmail());
            }
        });

        mBinding.tvCancel.setOnClickListener(view -> {
            finish();
        });
    }

    private void sendReq(String targetEmail) {
        String token = sp.getString("token", "");
        ThreadPoolManager.getInstance().execute(() -> {
            String json = GenerateJson.getApplyFriendJson(targetEmail);
            HttpStreamOP.postJson(AppProperties.FRIEND_REQ, token, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println("==========请求失败");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        System.out.println("=========回复失败");
                    }
                    String body = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        System.out.println("=======body=" + body);
                        System.out.println("=======jsonObj=" + jsonObject);
                        int code = jsonObject.getInt("code");
                        if (code != 1) {
                            System.out.println("=========code!=1");
                        }
                        JSONObject data = jsonObject.getJSONObject("data");
                        String state = data.getString("state");
                        if (state.equals("已申请")) {
                            System.out.println("======pending");
                            runOnUiThread(() -> {

                            });
                        } else if (state.equals("已申请")) {
                            System.out.println("======sended");
                            runOnUiThread(() -> {

                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }


}
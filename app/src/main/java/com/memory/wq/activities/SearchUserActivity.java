package com.memory.wq.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.adapters.SearchFriendAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.managers.UserManager;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.provider.HttpStreamOP;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.utils.GenerateJson;
import com.memory.wq.utils.MyToast;
import com.memory.wq.thread.ThreadPoolManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchUserActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_account;
    private TextView tv_cancel;

    private FriendManager friendManager;
    private SharedPreferences sp;
    private ListView lv_search;
    private List<FriendInfo> friendList;
    private SearchFriendAdapter adapter;
    private TextView tv_test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_layout);
        initView();
        initData();
    }


    private void initData() {
        friendManager = new FriendManager();
        sp = getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);

        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setNickname("Test");
        friendList = new ArrayList<>();
        friendList.add(friendInfo);


        adapter = new SearchFriendAdapter(this, friendList);
        lv_search.setAdapter(adapter);

    }

    private void initView() {
        et_account = (EditText) findViewById(R.id.et_account);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_test = (TextView) findViewById(R.id.tv_test);
        tv_test.setOnClickListener(this);
        lv_search = (ListView) findViewById(R.id.lv_search);

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = (FriendInfo) parent.getItemAtPosition(position);
                Intent intent = new Intent(SearchUserActivity.this,PersonalActivity.class);
                intent.putExtra(AppProperties.FRIENDINFO,friendInfo);
                startActivity(intent);
//                sendReq(friendInfo.getEmail());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_test:
                String account = et_account.getText().toString().trim();
                String token = sp.getString("token", "");
                friendManager.searchUser(SearchUserType.SEARCH_USER_TYPE_EMAIL,account, token, new ResultCallback<FriendInfo>() {
                    @Override
                    public void onSuccess(FriendInfo result) {
                        runOnUiThread(() -> {
                            friendList.clear();
                            friendList.add(result);
                            adapter.notifyDataSetChanged();

                        });

                        System.out.println("=================YES " + result.toString());
                    }

                    @Override
                    public void onError(String err) {

                    }
                });
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
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
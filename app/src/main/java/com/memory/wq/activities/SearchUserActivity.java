package com.memory.wq.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.adapters.SearchFriendAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.SearchUserLayoutBinding;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.constants.AppProperties;
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

    private static final String TAG = "WQ_SearchUserActivity";
    private FriendManager mFriendManager;
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

            mFriendManager.searchUser(SearchUserType.SEARCH_USER_TYPE_EMAIL, account, new ResultCallback<FriendInfo>() {
                @Override
                public void onSuccess(FriendInfo result) {
                    mFriendList.clear();
                    mFriendList.add(result);
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "[✓] initView searchUser #80" + result);
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
                intent.putExtra(AppProperties.PERSON_ID, friendInfo.getUuNumber());
                startActivity(intent);
                Log.d(TAG, "onItemClick: " + friendInfo.getUuNumber());
            }
        });

        mBinding.tvCancel.setOnClickListener(view -> {
            finish();
        });
    }

}
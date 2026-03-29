package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.memory.wq.R;
import com.memory.wq.adapters.SearchFriendAdapter;
import com.memory.wq.beans.FriendInfo;
import com.memory.wq.databinding.SearchUserLayoutBinding;
import com.memory.wq.enumertions.SearchUserType;
import com.memory.wq.interfaces.OnFriItemClickListener;
import com.memory.wq.managers.FriendManager;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.utils.ResultCallback;

import java.util.List;

public class SearchUserActivity extends BaseActivity<SearchUserLayoutBinding> {

    private static final String TAG = "WQ_SearchUserActivity";
    private final FriendManager mFriendManager = new FriendManager();
    private final SearchFriendAdapter mAdapter = new SearchFriendAdapter(new OnFriItemClickListenerImpl());
    private final OnTextWatcherImpl mTextWatcher = new OnTextWatcherImpl();

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

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mBinding.etAccount.removeTextChangedListener(mTextWatcher);
        }
        super.onDestroy();
    }

    private void initData() {
        loadUsers();
    }

    private void initView() {
        mBinding.ivBack.setOnClickListener(v -> finish());

        mBinding.rvSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvSearch.setAdapter(mAdapter);

        mBinding.etAccount.addTextChangedListener(mTextWatcher);
    }

    private void loadUsers(){
        searchUser("");
    }
    private void searchUser(String keyword) {
        mFriendManager.searchUserVague(keyword, new ResultCallback<List<FriendInfo>>() {
            @Override
            public void onSuccess(List<FriendInfo> result) {
                Log.d(TAG, "[✓] initView searchUser #80" + result);
                mAdapter.submitList(result);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private class OnTextWatcherImpl implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            String keyword = s.toString().trim();
            searchUser(keyword);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private class OnFriItemClickListenerImpl implements OnFriItemClickListener {

        @Override
        public void onItemClick(long targetId) {
            Intent intent = new Intent(SearchUserActivity.this, PersonInfoActivity.class);
            intent.putExtra(AppProperties.PERSON_ID, targetId);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick() {
        }

        @Override
        public void onUpdateClick(long targetId, boolean isAgree, String validMsg) {
        }
    }

}
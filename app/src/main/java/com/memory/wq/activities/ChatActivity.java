package com.memory.wq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.memory.wq.constants.AppProperties;
import com.memory.wq.fragment.ChatFragment;
import com.memory.wq.R;
import com.memory.wq.databinding.ActivityChatBinding;
import com.memory.wq.enumertions.ChatPage;
import com.memory.wq.fragment.ChatDetailFragment;
import com.memory.wq.vm.ChatViewModel;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {
    public static final String TAG = "WQ_ChatActivity";
    private ChatPage mCurrentPage;
    private ChatViewModel mChatVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String chatId = (String) intent.getSerializableExtra(AppProperties.CHAT_ID);
        mChatVM = new ViewModelProvider(this).get(ChatViewModel.class);
        mChatVM.setChatId(chatId);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }


    private void initView() {
        switchToPage(ChatPage.CHAT);
    }

    private void switchToPage(ChatPage target) {
        if (mCurrentPage == target) {
            Log.d(TAG, "[x] switchToPage #38");
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        hideOrRemoveFragment(ft, mCurrentPage);
        showOrAddFragment(ft, target);

        ft.commitAllowingStateLoss();
        mCurrentPage = target;
    }

    private void hideOrRemoveFragment(FragmentTransaction ft, ChatPage page) {
        if (page == null) {
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(page.name());
        if (fragment == null) {
            Log.d(TAG, "[x] hideOrRemoveFragment #56");
            return;
        }

        if (page.isReusable()) {
            ft.hide(fragment);
        } else {
            ft.remove(fragment);
        }
    }

    private void showOrAddFragment(FragmentTransaction ft, ChatPage page) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(page.name());
        if (fragment != null) {
            ft.show(fragment);
            return;
        }

        fragment = createFragment(page);
        if (fragment == null) {
            Log.d(TAG, "[x] showOrAddFragment #69");
            return;
        }

        ft.add(R.id.fl_container, fragment, page.name());
    }

    private Fragment createFragment(ChatPage page) {
        switch (page) {
            case CHAT:
                return new ChatFragment();
            case CHAT_DETAIL:
                return new ChatDetailFragment();
            default:
                return null;
        }
    }

}
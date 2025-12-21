package com.memory.wq.activities;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.memory.wq.R;
import com.memory.wq.databinding.ActivityMainBinding;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.Page;
import com.memory.wq.fragment.CowatchFragment;
import com.memory.wq.fragment.DiscoverFragment;
import com.memory.wq.fragment.HistoryFragment;
import com.memory.wq.fragment.MessageFragment;
import com.memory.wq.interfaces.IWebSocketListener;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;
import com.memory.wq.service.WebSocketMessage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements IWebSocketListener {

    private static final String TAG = "WQ_MainActivity";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Fragment mCurrentFragment;

    private final int PAGE_DISCOVER = 0;
    private final int PAGE_COWATCH = 1;
    private final int PAGE_MESSAGE = 2;
    private final int PAGE_HISTORY = 3;
    private final EnumSet<EventType> mEventTypes = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private WebService mWebService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        mBinding.llDiscover.performClick();
        startMyService();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void startMyService() {
        Intent intent = new Intent(this, WebService.class);
        startService(intent);
        MainActivityConn conn = new MainActivityConn();
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    @Override
    public EnumSet<EventType> getEvents() {
        return mEventTypes;
    }

    @Override
    public <T> void onMessage(WebSocketMessage<T> message) {
        switch (message.getEventType()) {
            case EVENT_TYPE_REQUEST_FRIEND:
                mBinding.tvMsgnum.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    private class MainActivityConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWebService = ((IWebSocketService) service).getService();
            mWebService.registerListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void switchFragment(Fragment to) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == to) {
            return;
        }

        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!to.isAdded()) {
            transaction.add(R.id.fl_bottom, to);
        } else {
            transaction.show(to);
        }
        transaction.commit();
        mCurrentFragment = to;
    }

    private void initView() {
        mBinding.llDiscover.setOnClickListener(view -> {
            updateBottomBar(Page.PAGE_DISCOVER.getValue());
            switchFragment(mFragmentList.get(0));
        });

        mBinding.llCoWatch.setOnClickListener(view -> {
            updateBottomBar(Page.PAGE_COWATCH.getValue());
            switchFragment(mFragmentList.get(1));
        });

        mBinding.llMsg.setOnClickListener(view -> {
            updateBottomBar(Page.PAGE_MESSAGE.getValue());
            switchFragment(mFragmentList.get(2));
        });

        mBinding.llHistory.setOnClickListener(view -> {
            updateBottomBar(Page.PAGE_HISTORY.getValue());
            switchFragment(mFragmentList.get(3));
        });

        initFragment();
    }

    private void updateBottomBar(int position) {
        resetTextColor();
        switch (position) {
            case PAGE_DISCOVER:
                mBinding.tvDiscover.setSelected(true);
                mBinding.tvDiscover.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_COWATCH:
                mBinding.tvCoWatch.setSelected(true);
                mBinding.tvCoWatch.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_MESSAGE:
                mBinding.tvMsg.setSelected(true);
                mBinding.tvMsg.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_HISTORY:
                mBinding.tvHistory.setSelected(true);
                mBinding.tvHistory.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
        }

    }

    private void resetTextColor() {
        mBinding.tvDiscover.setSelected(false);
        mBinding.tvCoWatch.setSelected(false);
        mBinding.tvMsg.setSelected(false);
        mBinding.tvHistory.setSelected(false);
        mBinding.tvDiscover.setTextColor(getResources().getColor(R.color.white_80));
        mBinding.tvCoWatch.setTextColor(getResources().getColor(R.color.white_80));
        mBinding.tvMsg.setTextColor(getResources().getColor(R.color.white_80));
        mBinding.tvHistory.setTextColor(getResources().getColor(R.color.white_80));
    }

    private void initFragment() {
        mFragmentList.add(new DiscoverFragment());
        mFragmentList.add(new CowatchFragment());
        mFragmentList.add(new MessageFragment());
        mFragmentList.add(new HistoryFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebService.unregisterListener(this);
    }
}
package com.memory.wq.activities;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memory.wq.R;
import com.memory.wq.enumertions.EventType;
import com.memory.wq.enumertions.Page;
import com.memory.wq.fragment.CowatchFragment;
import com.memory.wq.fragment.DiscoverFragment;
import com.memory.wq.fragment.HistoryFragment;
import com.memory.wq.fragment.MessageFragment;
import com.memory.wq.service.IWebSocketService;
import com.memory.wq.service.WebService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, WebService.WebSocketListener {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private Fragment currentFragment;
    private LinearLayout ll_discover;
    private LinearLayout ll_coWatch;
    private LinearLayout ll_msg;
    private LinearLayout ll_history;
    private TextView tv_discover;
    private TextView tv_coWatch;
    private TextView tv_msg;
    private TextView tv_history;

    private final int PAGE_DISCOVER = 0;
    private final int PAGE_COWATCH = 1;
    private final int PAGE_MESSAGE = 2;
    private final int PAGE_HISTORY = 3;
    private final EnumSet<EventType> eventTypes = EnumSet.of(EventType.EVENT_TYPE_REQUEST_FRIEND);
    private TextView tv_discoverNum;
    private ImageView iv_historynum;
    private TextView tv_msgnum;
    private TextView tv_coWatchNum;
    private WebService webService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        ll_discover.performClick();
        startMyService();
    }

    private void startMyService() {
        Intent intent = new Intent(this, WebService.class);
        startService(intent);
        MainActivityConn conn = new MainActivityConn();
        bindService(intent, conn, BIND_AUTO_CREATE);

    }

    @Override
    public EnumSet<EventType> getEvents() {
        return eventTypes;
    }

    @Override
    public void onEventMessage(EventType eventType) {
        runOnUiThread(() -> {
            switch (eventType) {
                case EVENT_TYPE_REQUEST_FRIEND:
                    if (!(currentFragment instanceof MessageFragment))
                        tv_msgnum.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {

    }

    private class MainActivityConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webService = ((IWebSocketService) service).getService();
            webService.registerListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_bottom, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    private void initView() {


        ll_discover = (LinearLayout) findViewById(R.id.ll_discover);
        ll_coWatch = (LinearLayout) findViewById(R.id.ll_coWatch);
        ll_msg = (LinearLayout) findViewById(R.id.ll_msg);
        ll_history = (LinearLayout) findViewById(R.id.ll_history);

        tv_discover = (TextView) findViewById(R.id.tv_discover);
        tv_coWatch = (TextView) findViewById(R.id.tv_coWatch);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_history = (TextView) findViewById(R.id.tv_history);

        tv_discoverNum = (TextView) findViewById(R.id.tv_discoverNum);
        tv_coWatchNum = (TextView) findViewById(R.id.tv_coWatchNum);
        tv_msgnum = (TextView) findViewById(R.id.tv_msgnum);
        iv_historynum = (ImageView) findViewById(R.id.iv_historynum);


        ll_discover.setOnClickListener(this);
        ll_coWatch.setOnClickListener(this);
        ll_msg.setOnClickListener(this);
        ll_history.setOnClickListener(this);

    }

    private void updateBottomBar(int position) {
        resetTextColor();
        switch (position) {
            case PAGE_DISCOVER:
                tv_discover.setSelected(true);
                tv_discover.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_COWATCH:
                tv_coWatch.setSelected(true);
                tv_coWatch.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_MESSAGE:
                tv_msg.setSelected(true);
                tv_msg.setTextColor(getResources().getColor(R.color.red));
                break;
            case PAGE_HISTORY:
                tv_history.setSelected(true);
                tv_history.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
        }

    }

    private void resetTextColor() {
        tv_discover.setSelected(false);
        tv_coWatch.setSelected(false);
        tv_msg.setSelected(false);
        tv_history.setSelected(false);
        tv_discover.setTextColor(getResources().getColor(R.color.white_80));
        tv_coWatch.setTextColor(getResources().getColor(R.color.white_80));
        tv_msg.setTextColor(getResources().getColor(R.color.white_80));
        tv_history.setTextColor(getResources().getColor(R.color.white_80));
    }

    private List<Fragment> initFragment() {

        fragmentList.add(new DiscoverFragment());
        fragmentList.add(new CowatchFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new HistoryFragment());
        return fragmentList;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_discover:
                switchBar(Page.PAGE_DISCOVER.getValue());
                switchFragment(fragmentList.get(0));
                break;
            case R.id.ll_coWatch:
                switchBar(Page.PAGE_COWATCH.getValue());
                switchFragment(fragmentList.get(1));
                break;
            case R.id.ll_msg:
                switchBar(Page.PAGE_MESSAGE.getValue());
                switchFragment(fragmentList.get(2));
                break;
            case R.id.ll_history:
                switchBar(Page.PAGE_HISTORY.getValue());
                switchFragment(fragmentList.get(3));
                break;
            default:
                break;
        }
    }

    private void switchBar(int position) {
        updateBottomBar(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webService.unregisterListener(this);
    }
}
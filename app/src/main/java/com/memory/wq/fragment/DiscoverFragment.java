package com.memory.wq.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.memory.wq.R;
import com.memory.wq.activities.AvatarActivity;
import com.memory.wq.activities.BaseActivity;
import com.memory.wq.activities.UploadPostActivity;
import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.UserInfoActivity;
import com.memory.wq.properties.AppProperties;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    private TextView tv_concern;
    private TextView tv_recommend;

    private Fragment currentFragment;
    private Fragment recommendFragment;
    private Fragment concernFragment;
    private DrawerLayout dl_main;
    private ImageView iv_open_drawer;
    private ImageView iv_avatar;
    private TextView tv_nickname;
    private TextView tv_nickname_hint;

    private NavigationView nv_side;


    private String email;
    private SharedPreferences sp;
    private TextView tv_usernumber;
    private ImageView iv_edit;
    private LinearLayout ll_userid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_layout, null, false);
        initView(view);
        initFragmentHolder();
        initDefaultFragment();
        initData();
        initDrawer();
        return view;
    }

    private void initData() {
        sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
    }

    private void initDrawer() {
        nv_side.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nv_info:
                    startActivity(new Intent(getContext(), UserInfoActivity.class));
                    break;
                case R.id.nv_logout:
                    //TODO
                    sp.edit().clear().commit();
                    BaseActivity.finishAll();
                    Intent intent = new Intent(getContext(), LaunchActivity.class);
                    startActivity(intent);
                    break;

            }
            return false;
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        String userName = sp.getString("userName", "");
        String avatarUrl = sp.getString("avatarUrl", "");
        long uuNumber = sp.getLong("uuNumber", -1L);

        Glide.with(getContext())
                .load(AppProperties.HTTP_SERVER_ADDRESS + avatarUrl)
                .circleCrop()
                .error(R.mipmap.icon_default_avatar)
                .into(iv_open_drawer);

        Glide.with(getContext())
                .load(AppProperties.HTTP_SERVER_ADDRESS + avatarUrl)
                .circleCrop()
                .error(R.mipmap.icon_default_avatar)
                .into(iv_avatar);

        tv_nickname.setText(userName);
        if (uuNumber==-1){
            ll_userid.setVisibility(View.GONE);
            tv_nickname_hint.setText("登录后可体验完整功能哦~");
        }

        tv_usernumber.setText(String.valueOf(uuNumber));
    }

    private void initFragmentHolder() {
        if (recommendFragment == null) {
            recommendFragment = new RecommmendFragment();
        }
        if (concernFragment == null) {
            concernFragment = new ConcernFragment();
        }
    }

    private void initDefaultFragment() {
        tv_recommend.performClick();
    }

    private void switchFragment(int position) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        switch (position) {
            case 0:
                if (!recommendFragment.isAdded()) {
                    transaction.add(R.id.fl_discover, recommendFragment);
                }
                transaction.show(recommendFragment);
                currentFragment = recommendFragment;
                break;
            case 1:
                if (!concernFragment.isAdded()) {
                    transaction.add(R.id.fl_discover, concernFragment);
                }
                transaction.show(concernFragment);
                currentFragment = concernFragment;
                break;
            default:
                break;
        }
        if (!transaction.isEmpty()) {
            transaction.commit();
        }
    }

    private void initView(View view) {
        tv_recommend = (TextView) view.findViewById(R.id.tv_recommend);
        tv_concern = (TextView) view.findViewById(R.id.tv_concern);
        iv_edit = (ImageView) view.findViewById(R.id.iv_edit);

        dl_main = (DrawerLayout) getActivity().findViewById(R.id.dl_main);
        iv_open_drawer = (ImageView) view.findViewById(R.id.iv_open_drawer);

        nv_side = (NavigationView) getActivity().findViewById(R.id.nv_side);
        View headerView = nv_side.getHeaderView(0);
        iv_avatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        tv_nickname = (TextView) headerView.findViewById(R.id.tv_nickname);
        tv_usernumber = (TextView) headerView.findViewById(R.id.tv_userid);
        tv_nickname_hint = (TextView) headerView.findViewById(R.id.tv_nickname_hint);
        ll_userid = (LinearLayout) headerView.findViewById(R.id.ll_userid);


        iv_open_drawer.setOnClickListener(this);
        tv_recommend.setOnClickListener(this);
        tv_concern.setOnClickListener(this);

        tv_nickname.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
        iv_edit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_recommend:
                switchFragment(0);
                resetcolor();
                tv_recommend.setTextColor(getResources().getColor(R.color.light_blue_600));
                break;
            case R.id.tv_concern:
                switchFragment(1);
                resetcolor();
                tv_concern.setTextColor(getResources().getColor(R.color.light_blue_600));
                break;
            case R.id.iv_open_drawer:
                dl_main.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_avatar:
                startActivity(new Intent(getContext(), AvatarActivity.class));
                break;
            case R.id.tv_nickname:

                break;
            case R.id.iv_edit:
                startActivity(new Intent(getContext(), UploadPostActivity.class));
                break;

        }
    }

    private void resetcolor() {
        tv_recommend.setTextColor(getResources().getColor(R.color.white_80));
        tv_concern.setTextColor(getResources().getColor(R.color.white_80));
    }
}

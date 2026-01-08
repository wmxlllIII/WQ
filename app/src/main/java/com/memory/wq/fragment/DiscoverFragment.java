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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.navigation.NavigationView;
import com.memory.wq.R;
import com.memory.wq.activities.AvatarActivity;
import com.memory.wq.activities.BaseActivity;
import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.ProfileActivity;
import com.memory.wq.activities.UploadPostActivity;
import com.memory.wq.constants.AppProperties;
import com.memory.wq.databinding.DiscoverLayoutBinding;
import com.memory.wq.managers.AccountManager;

public class DiscoverFragment extends Fragment {
    private Fragment currentFragment;
    private Fragment recommendFragment;
    private Fragment concernFragment;
    private DrawerLayout dl_main;
    private ImageView iv_avatar;
    private TextView tv_nickname;
    private TextView tv_nickname_hint;

    private NavigationView nv_side;

    private TextView tv_usernumber;
    private LinearLayout ll_userid;
    private DiscoverLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DiscoverLayoutBinding.inflate(inflater, container, false);
        initView();
        initFragmentHolder();
        initDefaultFragment();
        initData();
        initDrawer();
        return binding.getRoot();
    }

    private void initData() {
    }

    private void initDrawer() {
        nv_side.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nv_info:
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                    break;
                case R.id.nv_logout:
                    //TODO
                    SharedPreferences sp = getContext().getSharedPreferences(AppProperties.SP_NAME, Context.MODE_PRIVATE);
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

        Glide.with(getContext())
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .circleCrop()
                .into(binding.ivOpenDrawer);

        Glide.with(getContext())
                .load(AccountManager.getUserInfo().getAvatarUrl())
                .transform(new RoundedCorners(15))
                .into(iv_avatar);

        tv_nickname.setText(AccountManager.getUserInfo().getUsername());
        if (AccountManager.getUserId() <= 0) {
            ll_userid.setVisibility(View.GONE);
            tv_nickname_hint.setText("登录后可体验完整功能哦~");
        }

        tv_usernumber.setText(String.valueOf(AccountManager.getUserId()));
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
        binding.tvRecommend.performClick();
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

    private void initView() {

        dl_main = (DrawerLayout) getActivity().findViewById(R.id.dl_main);
        nv_side = (NavigationView) getActivity().findViewById(R.id.nv_side);
        View headerView = nv_side.getHeaderView(0);
        iv_avatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        tv_nickname = (TextView) headerView.findViewById(R.id.tv_nickname);
        tv_usernumber = (TextView) headerView.findViewById(R.id.tv_userid);
        tv_nickname_hint = (TextView) headerView.findViewById(R.id.tv_nickname_hint);
        ll_userid = (LinearLayout) headerView.findViewById(R.id.ll_userid);


        binding.ivOpenDrawer.setOnClickListener(v -> dl_main.openDrawer(Gravity.LEFT));

        binding.tvRecommend.setOnClickListener(v -> {
            switchFragment(0);
            resetcolor();
            binding.tvRecommend.setTextColor(getResources().getColor(R.color.light_blue_600));
        });

        binding.tvConcern.setOnClickListener(v -> {
            switchFragment(1);
            resetcolor();
            binding.tvConcern.setTextColor(getResources().getColor(R.color.light_blue_600));
        });

        tv_nickname.setOnClickListener(v -> {

        });

        iv_avatar.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AvatarActivity.class));
        });

        binding.ivEdit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UploadPostActivity.class));
        });

    }

    private void resetcolor() {
        binding.tvRecommend.setTextColor(getResources().getColor(R.color.white_80));
        binding.tvConcern.setTextColor(getResources().getColor(R.color.white_80));
    }
}

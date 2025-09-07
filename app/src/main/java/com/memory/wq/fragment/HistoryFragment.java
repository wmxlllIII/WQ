package com.memory.wq.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.memory.wq.R;
import com.memory.wq.activities.LaunchActivity;
import com.memory.wq.activities.MainActivity;
import com.memory.wq.managers.SessionManager;

public class HistoryFragment extends Fragment {

    private View view;
    private AppCompatActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_layout, container, false);
        initView(view);
        if (!SessionManager.isLoggedIn(mActivity)) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("未登录")
                    .setMessage("登录后即可体验完整功能哦~")
                    .setIcon(R.mipmap.ic_bannertest2)
                    .setNegativeButton("去登录", (dialogInterface, i) -> {
                        startActivity(new Intent(mActivity, LaunchActivity.class));
                        getActivity().finish();
                    })
                    .setPositiveButton("取消", (dialogInterface, i) -> {
                        //todo 隐藏底部
//                        view.findViewById(R.id.ll_already_login).setVisibility(View.GONE);
                        view.findViewById(R.id.layout_no_login).setVisibility(View.VISIBLE);
                    })
                    .setCancelable(false)
                    .show();
            return view;
        }
        return view;
    }

    private void initView(View view) {
        if (getContext() != null) {
            mActivity = (MainActivity) getContext();
        }
    }

}

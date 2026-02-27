package com.memory.wq.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.memory.wq.R;
import com.memory.wq.databinding.ActivityActorBinding;

public class ActorActivity extends BaseActivity<ActivityActorBinding> {

    public static final String TAG = "WQ_ActorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_actor;
    }
}
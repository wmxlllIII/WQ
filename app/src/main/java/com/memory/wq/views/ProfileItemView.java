package com.memory.wq.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.memory.wq.databinding.ItemProfileBinding;

public class ProfileItemView extends ConstraintLayout {

    private final ItemProfileBinding mBinding;

    public ProfileItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBinding = ItemProfileBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setTitle(@NonNull String title) {
        mBinding.tvTitle.setText(title);
    }

    public void setValue(@Nullable String value) {
        mBinding.tvValue.setText(value);
        mBinding.tvValue.setVisibility(
                value == null || value.isEmpty() ? GONE : VISIBLE
        );
    }

}

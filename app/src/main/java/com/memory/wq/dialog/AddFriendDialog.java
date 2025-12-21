package com.memory.wq.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.memory.wq.R;
import com.memory.wq.databinding.DialogRequesFriendBinding;

public class AddFriendDialog extends Dialog {

    private DialogRequesFriendBinding mBinding;
    private OnConfirmListener onConfirmListener;

    public AddFriendDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialogStyle);
        init();
    }

    private void init() {
        mBinding = DialogRequesFriendBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(mBinding.getRoot());
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mBinding.tvCancel.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onCancel();
            }
            dismiss();
        });

        mBinding.tvConfirm.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                String content = mBinding.etContent.getText().toString().trim();
                onConfirmListener.onConfirm(content);
            }
            dismiss();
        });
        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
        }

    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    public void setContent(String text) {
        mBinding.etContent.setText(text);
        mBinding.etContent.setSelection(text.length());
    }

    public interface OnConfirmListener {
        void onConfirm(String content);
        void onCancel();
    }

}


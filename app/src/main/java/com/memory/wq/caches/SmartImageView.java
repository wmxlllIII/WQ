package com.memory.wq.caches;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.memory.wq.R;
import com.memory.wq.properties.AppProperties;
import com.memory.wq.utils.ResultCallback;
import com.memory.wq.provider.SPOP;
import com.memory.wq.thread.ThreadPoolManager;

public class SmartImageView extends ImageView {
    private Context context;
    private Drawable mLoadingDrawable;
    private Drawable mFailedDrawable;

    public SmartImageView(Context context) {
        this(context, null);
    }

    public SmartImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartImageView, defStyleAttr, 0);
        mLoadingDrawable = typedArray.getDrawable(R.styleable.SmartImageView_onLoading);
        mFailedDrawable = typedArray.getDrawable(R.styleable.SmartImageView_onFail);
        typedArray.recycle();
    }

    public void setImageUrl(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            if (mFailedDrawable != null) {
                setImageDrawable(mFailedDrawable);
            }
            return;
        }
        if (mLoadingDrawable != null) {
            setImageDrawable(mLoadingDrawable);
        }
        ThreadPoolManager.getInstance().execute(() -> {
            SPOP spop = new SPOP(context);
            String token = spop.getUserInfo().getToken();
            new ImageCacheOP().getBitmapFromUrl(SmartImageView.this.context, AppProperties.HTTP_SERVER_ADDRESS+url, token, new ResultCallback<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    ((Activity) SmartImageView.this.context).runOnUiThread(() -> {
                        if (bitmap != null) {
                            setImageBitmap(bitmap);
                        }
                    });
                }

                @Override
                public void onError(String err) {
                    ((Activity) SmartImageView.this.context).runOnUiThread(() -> {
                        if (SmartImageView.this.mFailedDrawable != null) {
                            setImageDrawable(mFailedDrawable);

                        }
                    });
                    System.out.println("==========smERR"+err);
                }
            });
        });
    }
}

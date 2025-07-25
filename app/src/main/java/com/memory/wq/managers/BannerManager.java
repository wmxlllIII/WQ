package com.memory.wq.managers;


import android.os.Handler;
import android.os.Looper;

import androidx.viewpager2.widget.ViewPager2;

import com.memory.wq.adapters.BannerAdapter;

public class BannerManager {

    private final ViewPager2 viewPager;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    private static final int DEFAULT_INTERVAL = 5000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoScroll;
    private boolean isAutoScroll;


    public BannerManager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        initAutoScroll();
    }

    private void initAutoScroll() {
        autoScroll=()->{
            if (isAutoScroll && viewPager.getAdapter() !=null){
                int i = viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(i,true);
                handler.postDelayed(autoScroll,DEFAULT_INTERVAL);
            }
        };
    }

    public void setupWithAdapter(BannerAdapter adapter) {
        int initialPosition = Integer.MAX_VALUE / 2;
        viewPager.setCurrentItem(initialPosition, false);

        viewPager.setOffscreenPageLimit(3);
    }

    public void startAutoScroll() {
        if (isAutoScroll)
            return;
        isAutoScroll = true;
        handler.postDelayed(autoScroll, DEFAULT_INTERVAL);
    }

    public void pauseAutoScroll() {
        isAutoScroll = false;
        handler.removeCallbacks(autoScroll);
    }

    public void stopAutoScroll() {
        pauseAutoScroll();
        handler.removeCallbacksAndMessages(null);
    }
    

}

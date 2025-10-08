package com.memory.wq.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.memory.wq.fragment.WorksFragment;

public class VpHistoryAdapter extends FragmentStateAdapter {

    public VpHistoryAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WorksFragment();
            case 1:
                return new WorksFragment();
            case 2:
            case 3:
            default:
                return new WorksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
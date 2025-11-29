package com.memory.wq.vm;

import androidx.lifecycle.ViewModel;

import com.memory.wq.beans.PostInfo;

import java.util.ArrayList;
import java.util.List;

public class RecommendViewModel extends ViewModel {
    public List<PostInfo> postInfoList = new ArrayList<>();
    public int currentPage = 1;
    public boolean hasNextPage = true;
    public boolean isLoading = false;
    public List<Integer> bannerImageList = new ArrayList<>();
}

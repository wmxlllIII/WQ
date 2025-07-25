package com.memory.wq.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.memory.wq.R;
import com.memory.wq.adapters.FriendAdapter;
import com.memory.wq.adapters.MenuAdapter;
import com.memory.wq.adapters.ShareFriendsAdapter;
import com.memory.wq.beans.FriendInfo;

import java.util.List;

public class ShareFunctionMenu extends BottomSheetDialog {
    private Context context;
    private GridView gv_options;
    private String[] options;
    private List<FriendInfo> friendList;
    private OnOptionSelectedListener listener;
    private RecyclerView rl_friends;
    private ShareFriendsAdapter.OnFriendClickListener friendClickListener;

    public ShareFunctionMenu(@NonNull Context context , String[] options, List<FriendInfo> friendList) {
        super(context, R.style.BottomSheetDialog);
        this.context = context;
        this.options=options;
        this.friendList=friendList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(context).inflate(R.layout.item_function_layout, null);
        setContentView(view);
        initView(view);
        initData();
    }
    public void setOnFriendClickedListener(ShareFriendsAdapter.OnFriendClickListener listener) {
        this.friendClickListener = listener;
    }

    private void initData() {
        if (options != null) {
            MenuAdapter menuAdapter = new MenuAdapter(context, options);
            gv_options.setAdapter(menuAdapter);
            gv_options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (listener != null) {
                        String option = options[position];
                        listener.onOptionSelected(option);
                        dismiss();
                    }
                }
            });
            ShareFriendsAdapter friendsAdapter = new ShareFriendsAdapter(context, friendList);
            friendsAdapter.setOnFriendClickListener(new ShareFriendsAdapter.OnFriendClickListener() {
                @Override
                public void onFriendClick(FriendInfo friendInfo) {
                    if (friendClickListener!=null)
                        friendClickListener.onFriendClick(friendInfo);
                }
            });
            rl_friends.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            rl_friends.setAdapter(friendsAdapter);


        }

    }

    private void initView(View view) {
        gv_options = (GridView) view.findViewById(R.id.gv_options);
        rl_friends = (RecyclerView) view.findViewById(R.id.rl_friends);
    }



    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
    }


    public interface OnOptionSelectedListener {
        void onOptionSelected(String option);
    }
}

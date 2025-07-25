package com.memory.wq.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import com.memory.wq.R;

import java.util.ArrayList;
import java.util.List;
public class BaseActivity extends AppCompatActivity {
    public static List<Activity> activityList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        addActivity(this);

    }


    public static void addActivity(Activity activity){

        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){

        activityList.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity:activityList) {
            if ( !activity.isFinishing()){
                activity.finish();
            }
        }
        activityList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
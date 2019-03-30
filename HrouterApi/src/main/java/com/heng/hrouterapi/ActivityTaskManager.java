package com.heng.hrouterapi;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ActivityTaskManager {

    private final static ActivityTaskManager INSTANCE = new ActivityTaskManager();
    private ArrayList<Activity> activityList = new ArrayList<>();
    private List<Activity> frontActivity = new ArrayList<>();

    private ActivityTaskManager() {
    }

    public static ActivityTaskManager get() {
        return INSTANCE;
    }

    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                frontActivity.add(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                frontActivity.remove(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityList.remove(activity);
            }
        });
    }

    /**
     * 获取前台的activity
     *
     * @return
     */
    public Activity getFrontActivity() {
        if (frontActivity.size() > 0) return frontActivity.get(0);
        return null;
    }

    /**
     * 应用是否在前台
     *
     * @return
     */
    public boolean isFront() {
        return frontActivity.size() > 0;
    }
}

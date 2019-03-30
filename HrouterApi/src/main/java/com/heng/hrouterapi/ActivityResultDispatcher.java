package com.heng.hrouterapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.heng.hrouterapi.listener.ActivityResultCallBack;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * activity result回调管理类
 */
public class ActivityResultDispatcher {
    private static final ActivityResultDispatcher INSTANCE = new ActivityResultDispatcher();
    private Map<String, ActivityResultCallBack> mActivityResultCallBackMap = new HashMap<>();

    private ActivityResultDispatcher() {
    }

    public static ActivityResultDispatcher getInstance() {
        return INSTANCE;
    }

    /**
     * @param context                开启activity的Context
     * @param activityResultCallBack
     */
    public void addResultCallBack(Context context, ActivityResultCallBack activityResultCallBack) {
        if (context instanceof Activity && activityResultCallBack != null) {
            mActivityResultCallBackMap.put(context.getClass().getCanonicalName(), activityResultCallBack);
        }
    }

    /**
     * 分发 activity的result  可在BaseActivity中调用
     *
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void dispatchResult(Activity activity, int requestCode, int resultCode, Intent data) {
        ActivityResultCallBack activityResultCallBack = mActivityResultCallBackMap.remove(activity.getClass().getCanonicalName());
        if (activityResultCallBack != null) {
            activityResultCallBack.resultCallBack(requestCode, resultCode, data);
        }
    }

}

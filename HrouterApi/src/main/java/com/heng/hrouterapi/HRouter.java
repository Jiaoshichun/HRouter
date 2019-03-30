package com.heng.hrouterapi;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import com.heng.hrouterapi.exception.RouterException;
import com.heng.hrouterapi.interceptor.HRouterInterceptor;
import com.heng.hrouterapi.launcher.HRouteActivityLauncher;
import com.heng.hrouterapi.launcher.HRouterLauncher;
import com.heng.hrouterapi.listener.ActivityResultCallBack;
import com.heng.hrouterapi.listener.HRouterCallBack;

import java.io.Serializable;
import java.util.ArrayList;

public class HRouter {
    private HRouterBundle hRouterBundle;
    private HRouterLauncher launcher;
    private String url;

    private HRouter(String url) {
        this.url = url;
        this.hRouterBundle = new HRouterBundle(url);
    }

    public static HRouter create(String url) {
        if (!isValidUri(url)) {
            throw new RouterException(HRouterConstant.ERROR_CODE_URL, String.format("%s is not uri", url));
        }
        return new HRouter(url);
    }

    public static HRouter createByAction(String action) {
        return new HRouter(null).setAction(action);
    }

    /**
     * 设置ActivityResult结果回调
     *
     * @param resultCallBack
     */
    public HRouter setResultCallBack(ActivityResultCallBack resultCallBack) {
        hRouterBundle.setResultCallBack(resultCallBack);
        return this;
    }

    /**
     * 监听router跳转结果
     *
     * @param routerCallBack
     */
    public HRouter setRouterCallBack(HRouterCallBack routerCallBack) {
        hRouterBundle.setRouterCallBack(routerCallBack);
        return this;
    }

    public void open(Context context) {
        getLauncher().open(context, hRouterBundle);
    }

    public void open(Fragment fragment) {
        getLauncher().open(fragment.getActivity(), hRouterBundle);
    }

    private HRouterLauncher getLauncher() {
        if (launcher == null) {
            launcher = new HRouteActivityLauncher();
        }
        return launcher;
    }

    public void setLauncher(HRouterLauncher launcher) {
        this.launcher = launcher;
    }

    public HRouter requestCode(int requestCode) {
        hRouterBundle.requestCode(requestCode);
        return this;
    }

    public HRouter addExtra(String key, String value) {
        hRouterBundle.getBundle().putString(key, value);
        return this;
    }

    /**
     * 用于拦截器拦截跳转后恢复跳转
     * 注意避免调用该方法时 造成一直拦截的死循环
     *
     * @param hRouterBundle
     */
    public static HRouter resume(HRouterBundle hRouterBundle) {
        HRouter hRouter = create(hRouterBundle.getUrl());
        hRouter.hRouterBundle = hRouterBundle;
        return hRouter;
    }

    public HRouter addExtra(String key, boolean value) {
        hRouterBundle.getBundle().putBoolean(key, value);
        return this;
    }

    public HRouter addExtra(String key, int value) {
        hRouterBundle.getBundle().putInt(key, value);
        return this;
    }

    public HRouter addExtra(String key, double value) {
        hRouterBundle.getBundle().putDouble(key, value);
        return this;
    }

    public HRouter addExtra(String key, float value) {
        hRouterBundle.getBundle().putFloat(key, value);
        return this;
    }

    public HRouter addExtra(String key, char value) {
        hRouterBundle.getBundle().putChar(key, value);
        return this;
    }

    public HRouter addExtra(String key, Serializable value) {
        hRouterBundle.getBundle().putSerializable(key, value);
        return this;
    }

    public HRouter addExtra(String key, Parcelable value) {
        hRouterBundle.getBundle().putParcelable(key, value);
        return this;
    }

    public HRouter addPermission(String... permissions) {
        hRouterBundle.addPermission(permissions);
        return this;
    }

    public HRouter addInterceptor(HRouterInterceptor... interceptors) {
        hRouterBundle.addInterceptor(interceptors);
        return this;
    }

    public HRouter addExtra(String key, ArrayList<String> value) {
        hRouterBundle.getBundle().putStringArrayList(key, value);
        return this;
    }

    public HRouter addExtra(String key, long value) {
        hRouterBundle.getBundle().putLong(key, value);
        return this;
    }

    public HRouter addExtra(Bundle bundle) {
        hRouterBundle.getBundle().putAll(bundle);
        return this;
    }

    public HRouter addFlag(int flag) {
        hRouterBundle.addFlag(flag);
        return this;
    }

    public HRouter setAction(String action) {
        hRouterBundle.setAction(action);
        return this;
    }

    public HRouter setData(Uri data) {
        hRouterBundle.setData(data);
        return this;
    }

    public HRouter setType(String type) {
        hRouterBundle.setType(type);
        return this;
    }

    public HRouter setOptions(Bundle options) {
        hRouterBundle.setOptions(options);
        return this;
    }

    public HRouter addCategory(String category) {
        hRouterBundle.addCategory(category);
        return this;
    }

    public HRouter setAnim(int enterAnim, int exitAnim) {
        hRouterBundle.setEnterAnim(enterAnim);
        hRouterBundle.setExitAnim(exitAnim);
        return this;
    }

    /**
     * 判断链接是否有效
     *
     * @param url
     * @return
     */
    public static boolean isValidUri(String url) {
        Uri uri = Uri.parse(url);
        return uri != null && !TextUtils.isEmpty(uri.getScheme()) && !TextUtils.isEmpty(uri.getHost());
    }
}

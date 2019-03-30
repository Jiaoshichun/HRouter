package com.heng.hrouterapi;

import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import com.heng.hrouterapi.config.HRouterConfiguration;
import com.heng.hrouterapi.interceptor.HRouterInterceptor;
import com.heng.hrouterapi.listener.ActivityResultCallBack;
import com.heng.hrouterapi.listener.HRouterCallBack;
import com.heng.hrouterapi.rule.HRouterRuleData;

import java.util.*;

public class HRouterBundle {
    private String url;
    private Bundle extraBundle = new Bundle();
    private Bundle options;
    private int requestCode;
    private ActivityResultCallBack activityResultCallBack;
    private HRouterCallBack routerCallBack;
    private int mFlag;
    private String mAction;
    private Uri data;
    private String type;
    private int enterAnim, exitAnim;
    private Set<String> permissions = new HashSet<>();
    private List<HRouterInterceptor> interceptors = new ArrayList<>();
    private String className;
    private List<String> categoryList = new ArrayList<>();

    HRouterBundle(String url) {
        this.url = url;
        init();

    }

    private void init() {
        interceptors.addAll(HRouterConfiguration.get().getGlobalInterceptor());
        if (!TextUtils.isEmpty(url)) {
            parseUrl();
            HRouterRuleData ruleData = HRouterConfiguration.get().getRouterRule().get(getBaseUrl(url));
            if (ruleData != null) {
                className = ruleData.getClzName();
                Class<? extends HRouterInterceptor>[] classes = ruleData.getInterceptors();
                for (Class<? extends HRouterInterceptor> clazz : classes) {
                    try {
                        interceptors.add(clazz.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                permissions.addAll(Arrays.asList(ruleData.getPermissions()));
            }

        }
    }

    public String getClassName() {
        return className;
    }

    private String getBaseUrl(String url) {
        if (TextUtils.isEmpty(url)) return null;
        Uri uri = Uri.parse(url);
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

    private void parseUrl() {
        Uri parse = Uri.parse(url);
        Set<String> names = parse.getQueryParameterNames();
        for (String n : names) {
            extraBundle.putString(n, parse.getQueryParameter(n));
        }
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public List<HRouterInterceptor> getInterceptors() {
        return interceptors;
    }

    public Bundle getBundle() {
        return extraBundle;
    }

    void requestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    void setResultCallBack(ActivityResultCallBack resultCallBack) {
        this.activityResultCallBack = resultCallBack;
    }

    public ActivityResultCallBack getActivityResultCallBack() {
        return activityResultCallBack;
    }

    public HRouterCallBack getRouterCallBack() {
        return routerCallBack;
    }

    public String getUrl() {
        return url;
    }

    void setRouterCallBack(HRouterCallBack routerCallBack) {
        this.routerCallBack = routerCallBack;
    }


    public void addFlag(int flag) {
        mFlag |= flag;
    }

    public int getFlag() {
        return mFlag;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public void setEnterAnim(int enterAnim) {
        this.enterAnim = enterAnim;
    }

    public void setOptions(Bundle options) {
        this.options = options;
    }

    public Bundle getOptions() {
        return options;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public void setExitAnim(int exitAnim) {
        this.exitAnim = exitAnim;
    }

    public Uri getData() {
        return data;
    }

    public void setData(Uri data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void addPermission(String... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public void addInterceptor(HRouterInterceptor... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCategoryList() {
        return new ArrayList<>(categoryList);
    }

    public void addCategory(String category) {
        categoryList.add(category);
    }
}

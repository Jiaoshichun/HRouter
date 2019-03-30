package com.heng.hrouterapi.listener;

import com.heng.hrouterapi.HRouterBundle;

/**
 * 跳转回调
 */
public interface HRouterCallBack {


    void onSuccess();

    void onError(int errorCode, String url,HRouterBundle hRouterBundle);
}

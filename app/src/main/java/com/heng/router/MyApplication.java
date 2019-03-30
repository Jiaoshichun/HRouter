package com.heng.router;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.heng.hrouterapi.HRouterBundle;
import com.heng.hrouterapi.config.HRouterConfiguration;
import com.heng.hrouterapi.interceptor.HRouterInterceptor;
import com.heng.hrouterapi.listener.HRouterCallBack;
import com.heng.routerannotation.HRouterConfig;
import com.shi.router.HRouterRuleCreatorImpl;

@HRouterConfig(baseUrl = "hrouter://", pack = "com.shi.router")
public class MyApplication extends Application {
    private final static String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        HRouterConfiguration.get().addGlobalInterceptor(new HRouterInterceptor() {
            @Override
            public boolean intercept(Context context, String uri, HRouterBundle extras) {
                Log.e(TAG, "intercept");
                return false;
            }

            @Override
            public void onIntercepted(Context context, String uri, HRouterBundle extras) {

            }
        });
        HRouterConfiguration.get().init(this)//初始化方法
                .addRouterRuleCreator(new HRouterRuleCreatorImpl())//传入规则生成器
                .addGlobalHRouterCallBack(new HRouterCallBack() {//设置全局回调
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess");
                    }

                    @Override
                    public void onError(int errorCode, String url, HRouterBundle hRouterBundle) {
                        Log.e(TAG, "onError errorCode" + errorCode + "  url:" + url);
                    }

                });
    }
}

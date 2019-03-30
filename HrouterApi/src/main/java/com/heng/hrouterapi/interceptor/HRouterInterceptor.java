package com.heng.hrouterapi.interceptor;

import android.content.Context;
import com.heng.hrouterapi.HRouterBundle;

/**
 * An interceptor interface
 */
public interface HRouterInterceptor {

    boolean intercept(Context context, String uri, HRouterBundle extras);

    void onIntercepted(Context context, String uri, HRouterBundle extras);
}
package com.heng.hrouterapi.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import com.heng.hrouterapi.*;
import com.heng.hrouterapi.config.HRouterConfiguration;
import com.heng.hrouterapi.config.PermissionProcessor;
import com.heng.hrouterapi.exception.RouterException;
import com.heng.hrouterapi.interceptor.HRouterInterceptor;
import com.heng.hrouterapi.listener.HRouterCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HRouteActivityLauncher implements HRouterLauncher {

    private HRouterBundle hRouterBundle;
    private Context context;

    @Override
    public void open(Context context, HRouterBundle hRouterBundle) {
        if (context == null) {
            throw new RouterException(HRouterConstant.ERROR_CODE_OTHER, "context mast not be empty");
        }
        this.context = context;
        if (!(context instanceof Activity) && ActivityTaskManager.get().getFrontActivity() != null) {
            this.context = ActivityTaskManager.get().getFrontActivity();
        }
        this.hRouterBundle = hRouterBundle;
        try {
            if (TextUtils.isEmpty(hRouterBundle.getClassName()) && hRouterBundle.getAction() == null) {
                throw new RouterException(HRouterConstant.ERROR_CODE_NO_FIND, String.format("router %s not find", hRouterBundle.getUrl()));
            }

            processInterceptor(hRouterBundle, hRouterBundle.getInterceptors());
            processPermission(hRouterBundle, hRouterBundle.getPermissions());
            openActivity();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RouterException) {
                RouterException routerException = (RouterException) e;
                routerError(hRouterBundle, routerException.getType());
            } else {
                routerError(hRouterBundle, HRouterConstant.ERROR_CODE_OTHER);
            }
        }
    }


    /**
     * 真正的开启Activity
     */
    private void openActivity() {
        Intent intent = createIntent();
        if (context instanceof Activity) {
            Activity activity = (Activity) this.context;
            if (hRouterBundle.getOptions() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activity.startActivityForResult(intent, hRouterBundle.getRequestCode(), hRouterBundle.getOptions());
            } else {
                activity.startActivityForResult(intent, hRouterBundle.getRequestCode());
            }
            if (hRouterBundle.getActivityResultCallBack() != null) {
                ActivityResultDispatcher.getInstance().addResultCallBack(this.context,
                        hRouterBundle.getActivityResultCallBack());
            }
            activity.overridePendingTransition(hRouterBundle.getEnterAnim(), hRouterBundle.getExitAnim());
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }

    /**
     * 生成intent
     *
     * @return
     */
    private Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(hRouterBundle.getAction());
        intent.addFlags(hRouterBundle.getFlag());
        intent.putExtras(hRouterBundle.getBundle());
        if (hRouterBundle.getCategoryList().size() > 0) {
            for (String category : hRouterBundle.getCategoryList()) {
                intent.addCategory(category);
            }
        }
        if (!TextUtils.isEmpty(hRouterBundle.getClassName())) {
            intent.setClassName(context, hRouterBundle.getClassName());
        }
        if (hRouterBundle.getData() != null && hRouterBundle.getType() != null) {
            intent.setDataAndType(hRouterBundle.getData(), hRouterBundle.getType());
        } else {
            if (hRouterBundle.getData() != null) {
                intent.setData(hRouterBundle.getData());
            }
            if (hRouterBundle.getType() != null) {
                intent.setType(hRouterBundle.getType());
            }
        }

        return intent;
    }

    /**
     * 处理activity所需权限
     *
     * @param hRouterBundle
     * @param permissions
     */
    private void processPermission(final HRouterBundle hRouterBundle, Collection<String> permissions) {
        if (permissions == null || permissions.size() == 0 || HRouterConfiguration.get().getPermissionProcessor() == null)
            return;
        ArrayList<String> permissionList = new ArrayList<>();//需要申请的权限列表
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(p);
            }
        }
        if (permissionList.size() == 0) return;

        PermissionProcessor permissionProcessor = HRouterConfiguration.get().getPermissionProcessor();
        permissionProcessor.handlerPermission(context, permissionList.toArray(new String[0]), new PermissionProcessor.CallBack() {
            @Override
            public void onSuccess() {
                HRouter.resume(hRouterBundle).open(context);
            }

            @Override
            public void onFail() {
                routerError(hRouterBundle, HRouterConstant.ERROR_CODE_PERMISSIONI_FORBIDDEN);
            }
        });

        throw new RouterException(HRouterConstant.ERROR_CODE_PERMISSIONI, "");
    }

    /**
     * 处理拦截器
     *
     * @param routerBundle
     * @param interceptors
     */
    private void processInterceptor(HRouterBundle routerBundle, List<HRouterInterceptor> interceptors) {
        for (HRouterInterceptor interceptor : interceptors) {
            if (interceptor.intercept(context, routerBundle.getUrl(), routerBundle)) {
                interceptor.onIntercepted(context, routerBundle.getUrl(), routerBundle);
                throw new RouterException(HRouterConstant.ERROR_CODE_INTERCEPTOR, String.format("router be %s intercepted"
                        , interceptor.getClass().getCanonicalName()));
            }
        }
    }


    private void routerError(HRouterBundle hRouterBundle, int errorCode) {
        for (HRouterCallBack callBack : HRouterConfiguration.get().getGlobalRouterCallBack()) {
            if (callBack != null) {
                callBack.onError(errorCode, hRouterBundle.getUrl(), hRouterBundle);
            }
        }
        if (hRouterBundle.getRouterCallBack() != null) {
            hRouterBundle.getRouterCallBack().onError(errorCode, hRouterBundle.getUrl(), hRouterBundle);
        }
    }
}

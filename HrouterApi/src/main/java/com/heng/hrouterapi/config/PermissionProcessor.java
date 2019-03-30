package com.heng.hrouterapi.config;

import android.content.Context;

public interface PermissionProcessor {
    void handlerPermission(Context context, String[] permission, CallBack callBack);

    interface CallBack {
        void onSuccess();

        void onFail();
    }
}

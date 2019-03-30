package com.heng.hrouterapi.config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.yanzhenjie.permission.*;

import java.util.Arrays;
import java.util.List;

public class DefaultPermissionProcessor implements PermissionProcessor {
    @Override
    public void handlerPermission(final Context context, String[] permission, final CallBack callBack) {
        AndPermission.with(context)
                .runtime()
                .permission(permission)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (callBack != null) {
                            callBack.onSuccess();
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (callBack != null) {
                            callBack.onFail();
                        }
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSettingDialog(context, permissions);
                        }
                    }
                })
                .start();
    }

    final class RuntimeRationale implements Rationale<List<String>> {

        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            if (!(context instanceof Activity)) return;
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = String.format("需要以下权限才可继续使用\n%s", TextUtils.join("\n", permissionNames));

            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }

    }

    /**
     * Display setting dialog.
     */
    private void showSettingDialog(final Context context, final List<String> permissions) {
        if (!(context instanceof Activity)) return;
        if(AndPermission.hasPermissions(context,permissions.toArray(new String[0]))){
            return;
        }
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = String.format("以下权限被禁止，请去设置页开启对应权限\n%s", TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission(context);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private void setPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {

                    }
                })
                .start();
    }
}

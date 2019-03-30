package com.heng.hrouterapi.rule;

import com.heng.hrouterapi.interceptor.HRouterInterceptor;

public class HRouterRuleData {
    private String clzName;

    public HRouterRuleData(String clzName) {
        this.clzName = clzName;
    }

    public String getClzName() {
        return clzName;
    }

    private Class<? extends HRouterInterceptor>[] interceptors = new Class[0];
    private String[] permissions = new String[0];

    public Class<? extends HRouterInterceptor>[] getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(Class<? extends HRouterInterceptor>... interceptors) {
        this.interceptors = interceptors;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String... permissions) {
        this.permissions = permissions;
    }
}

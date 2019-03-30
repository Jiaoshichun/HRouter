package com.heng.hrouterapi.config;

import android.app.Application;
import android.support.annotation.NonNull;
import com.heng.hrouterapi.ActivityTaskManager;
import com.heng.hrouterapi.listener.HRouterCallBack;
import com.heng.hrouterapi.interceptor.HRouterInterceptor;
import com.heng.hrouterapi.rule.HRouterRuleCreator;
import com.heng.hrouterapi.rule.HRouterRuleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HRouterConfiguration {

    private Map<String, HRouterRuleData> routerRule = new HashMap<>();

    private HRouterConfiguration() {
    }

    private final static HRouterConfiguration INSTANCE = new HRouterConfiguration();

    public static HRouterConfiguration get() {
        return INSTANCE;
    }

    public HRouterConfiguration init(Application application) {
        ActivityTaskManager.get().init(application);
        return this;
    }

    private List<HRouterInterceptor> globalInterceptor = new ArrayList<>();
    private List<HRouterCallBack> globalRouterCallBack = new ArrayList<>();
    private PermissionProcessor permissionProcessor = new DefaultPermissionProcessor();

    /**
     * 设置跳转规则生成器  一般调用该方法 初始化生成器
     *
     * @param creator HRouterRuleCreatorImpl 注解自动生成的规则生成器实例
     *                （ 导入项目后 在需要的activity添加{@link com.heng.routerannotation.HRouterRule}rebuild后自动生成）
     *                也可传入自定义的生成器
     */
    public HRouterConfiguration addRouterRuleCreator(HRouterRuleCreator creator) {
        if (creator != null) {
            routerRule.putAll(creator.createHRouterRule());
        }
        return this;
    }

    /**
     * 添加 单个跳转规则
     *
     * @param clazz
     * @param hRouterRuleData
     */
    public HRouterConfiguration addRouterRule(String clazz, HRouterRuleData hRouterRuleData) {
        routerRule.put(clazz, hRouterRuleData);
        return this;
    }

    /**
     * 添加多个跳转个规则
     *
     * @param routerMap
     */
    public HRouterConfiguration addRuouteRuleMap(Map<String, HRouterRuleData> routerMap) {
        routerRule.putAll(routerMap);
        return this;
    }

    /**
     * 添加全局的拦截器
     *
     * @param interceptor
     */
    public HRouterConfiguration addGlobalInterceptor(HRouterInterceptor interceptor) {
        globalInterceptor.add(interceptor);
        return this;
    }

    public Map<String, HRouterRuleData> getRouterRule() {
        return routerRule;
    }


    public List<HRouterInterceptor> getGlobalInterceptor() {
        return globalInterceptor;
    }

    public List<HRouterCallBack> getGlobalRouterCallBack() {
        return globalRouterCallBack;
    }

    /**
     * 添加全局的路由跳转回调
     *
     * @param callBack
     */
    public HRouterConfiguration addGlobalHRouterCallBack(HRouterCallBack callBack) {
        globalRouterCallBack.add(callBack);
        return this;
    }

    public PermissionProcessor getPermissionProcessor() {
        return permissionProcessor;
    }

    /**
     * 设置跳转时activity需要
     *
     * @param permissionProcessor
     */
    public HRouterConfiguration setPermissionProcessor(@NonNull PermissionProcessor permissionProcessor) {
        this.permissionProcessor = permissionProcessor;
        return this;
    }
}

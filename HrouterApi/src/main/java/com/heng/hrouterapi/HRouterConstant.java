package com.heng.hrouterapi;

public interface HRouterConstant {
    int ERROR_CODE_NO_FIND = 1;//没有发现对应的界面
    int ERROR_CODE_INTERCEPTOR = 2;//拦截器拦截
    int ERROR_CODE_OTHER = 3;//其他错误
    int ERROR_CODE_PERMISSIONI = 4;//申请权限时会抛出该异常
    int ERROR_CODE_PERMISSIONI_FORBIDDEN = 5;//权限被拒绝时抛出该异常
    int ERROR_CODE_URL = -1;//跳转格式错误
}

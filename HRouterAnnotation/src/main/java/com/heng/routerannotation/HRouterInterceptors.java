package com.heng.routerannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//router Interceptors
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface HRouterInterceptors {
    Class[] value();
}

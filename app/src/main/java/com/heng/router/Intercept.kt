package com.heng.router

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.heng.hrouterapi.HRouter
import com.heng.hrouterapi.HRouterBundle
import com.heng.hrouterapi.interceptor.HRouterInterceptor

class Intercept : HRouterInterceptor {
    val TAG="Intercept"
    override fun intercept(context: Context?, uri: String?, extras: HRouterBundle?): Boolean {
        Log.e(TAG,"intercept")
        if (extras?.bundle?.getBoolean("is") == true) {
            return false
        }
        return true
    }

    override fun onIntercepted(context: Context?, uri: String?, extras: HRouterBundle?) {
        Log.e(TAG,"onIntercepted")
        extras?.let {
            it.bundle.putBoolean("is", true)
            HRouter.resume(it).open(context)
        }
    }
}
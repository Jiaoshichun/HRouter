package com.heng.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.heng.routerannotation.HRouterInterceptors
import com.heng.routerannotation.HRouterRule

@HRouterRule(ONE)
@HRouterInterceptors(Intercept::class)
class OneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)
        Toast.makeText(this, intent.getStringExtra("content"), Toast.LENGTH_LONG).show()
    }

    override fun finish() {
        setResult(Activity.RESULT_OK, Intent().putExtra("result", "结果"))
        super.finish()
    }


}

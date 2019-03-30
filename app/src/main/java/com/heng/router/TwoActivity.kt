package com.heng.router

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.heng.routerannotation.HRouterRule

@HRouterRule(TWO)
class TwoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)
        findViewById<TextView>(R.id.txt).text="Two"
    }
}

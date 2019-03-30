package com.heng.router

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.heng.hrouterapi.ActivityResultDispatcher

abstract class BaseActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ActivityResultDispatcher.getInstance().dispatchResult(this, requestCode, resultCode, data)
    }
}
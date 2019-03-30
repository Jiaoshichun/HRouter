package com.heng.router

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import com.heng.hrouterapi.HRouter
import com.heng.hrouterapi.listener.ActivityResultCallBack
import com.heng.routerannotation.HRouterRule
import com.yanzhenjie.permission.AndPermission
import java.io.File

const val ONE = "hrouter://one"
const val TWO = "hrouter://two"

@HRouterRule("hrouter://mainActivity", "hrouter://main")
class MainActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btn1 ->  HRouter.createByAction(MediaStore.ACTION_IMAGE_CAPTURE)
                    .addPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                    .addExtra(
                        MediaStore.EXTRA_OUTPUT, AndPermission.getFileUri(
                            this, File(Environment.getExternalStorageDirectory(), "hrouter.jpg")
                        )
                    )
                    .requestCode(110)
                    .open(this)
                R.id.btn2 -> HRouter.createByAction(Intent.ACTION_PICK)
                    .setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    .setType("image/jpeg")
                    .setResultCallBack(object : ActivityResultCallBack {
                        override fun resultCallBack(requestCode: Int, resultCode: Int, data: Intent?) {
                            data?.let {
                                Log.e("====", it.data.toString())
                            }

                        }

                    })
                    .open(this)
                R.id.btn3 -> HRouter.create("hrouter://wu").open(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn1).setOnClickListener(this)
        findViewById<Button>(R.id.btn2).setOnClickListener(this)
        findViewById<Button>(R.id.btn3).setOnClickListener(this)
    }

}

package com.heng.hrouterapi.listener;

import android.content.Intent;

public interface ActivityResultCallBack {
    void resultCallBack(int requestCode, int resultCode, Intent data);
}

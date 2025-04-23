package com.example.myapplication.javabean;

import android.graphics.Bitmap;

public interface AvatarCallBack {
    void sendBitmapToActivity(Bitmap bitmap);
    Bitmap getBitmapFromActivity(Bitmap bitmap);
}

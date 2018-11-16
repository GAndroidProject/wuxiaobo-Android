package com.xiaoe.shop.wxb.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = LogUtils.class.getSimpleName();

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}
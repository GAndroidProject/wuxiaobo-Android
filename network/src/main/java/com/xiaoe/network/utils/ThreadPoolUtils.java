package com.xiaoe.network.utils;


import android.os.Handler;

/**
 * Created by Administrator on 2015/12/1.
 */
public class ThreadPoolUtils {
    public static void runTaskOnThread(Runnable runnable){
        ThreadPoolFactory.getCommonThreadPool().execute(runnable);
    }
    private static Handler handler = new Handler();
    public static void runTaskOnUIThread(Runnable runnable){
        handler.post(runnable);
    }
}

package com.xiaoe.shop.wxb.utils;

import com.xiaoe.common.app.XiaoeApplication;

/**
 * @author flynnWang
 * @date 2018/10/21
 * <p>
 * 描述：
 */
public class AndroidUtilities {

    private AndroidUtilities() {
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            XiaoeApplication.applicationHandler.post(runnable);
        } else {
            XiaoeApplication.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        XiaoeApplication.applicationHandler.removeCallbacks(runnable);
    }
}

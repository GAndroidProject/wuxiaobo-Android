package com.xiaoe.common.utils;

import android.content.Context;

import com.meituan.android.walle.WalleChannelReader;

/**
 * @author flynnWang
 * @date 2019/1/9
 * <p>
 * 描述：
 */
public class CommonUtils {

    private CommonUtils() {
    }

    public static String getChannel(Context context) {
        return WalleChannelReader.getChannel(context, "guanwang");
    }
}

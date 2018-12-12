package com.xiaoe.shop.wxb.utils;

import android.content.Context;

import com.meituan.android.walle.WalleChannelReader;

/**
 * @author flynnWang
 * @date 2018/12/12
 * <p>
 * 描述：
 */
public class Utils {

    private Utils() {
    }

    public static String getChannel(Context context) {
        return WalleChannelReader.getChannel(context, "guanwang");
    }
}

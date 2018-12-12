package com.xiaoe.shop.wxb.common.datareport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xiaoe.shop.wxb.utils.Utils;

import java.util.Map;

/**
 * @author flynnWang
 * @date 2018/12/12
 * <p>
 * 描述：
 */
public class EventReportManager {

    private static final String TAG = "EventReportManager";

    public static void onEvent(Context context, String eventID) {
        // 默认添加渠道号
        onEvent(context, eventID, Utils.getChannel(context));
    }

    public static void onEvent(Context context, String eventID, String label) {
        Log.d(TAG, "onEvent: eventID-> " + eventID + " , label-> " + label);

        MobclickAgent.onEvent(context, eventID, label);
    }

    public static void onEvent(Context context, String eventID, @NonNull Map<String, String> map) {
        // 默认添加渠道号
        map.put("channel", Utils.getChannel(context));
        Log.d(TAG, "onEvent: eventID-> " + eventID + " , map-> " + map);

        MobclickAgent.onEvent(context, eventID, map);
    }
}

package com.xiaoe.shop.wxb.common.datareport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xiaoe.shop.wxb.utils.Utils;

import java.util.HashMap;
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
        map.put(MobclickEvent.CHANNEL, Utils.getChannel(context));
        Log.d(TAG, "onEvent: eventID-> " + eventID + " , map-> " + map);

        MobclickAgent.onEvent(context, eventID, map);
    }

    /**
     * 统计时长
     *
     * @param context
     * @param eventID
     * @param duration 开发者需要自己计算音乐播放时长
     */
    public static void onEventValue(Context context, String eventID, int duration) {
        // 默认添加渠道号
        Map<String, String> mapValue = new HashMap<>(1);
        mapValue.put(MobclickEvent.CHANNEL, Utils.getChannel(context));
        Log.d(TAG, "onEventValue: eventID-> " + eventID + " , duration-> " + duration + " , map-> " + mapValue);

        MobclickAgent.onEventValue(context, eventID, mapValue, duration);
    }
}

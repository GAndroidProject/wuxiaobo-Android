package com.xiaoe.shop.wxb.utils;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.shop.wxb.base.XiaoeActivity;

/**
 * activity 处理类
 */
public class ActivityCollector {

    private static List<XiaoeActivity> activities = new ArrayList<>();

    public static void addActivity(XiaoeActivity activity) {
        activities.add(activity);
    }

    public static void removeActivity(XiaoeActivity activity) {
        activities.remove(activity);
    }

    public static XiaoeActivity getActivity(int position){
        if(activities == null || position >= activities.size()){
            return null;
        }
        return activities.get(position);
    }

    public static void finishAll() {
        for (XiaoeActivity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

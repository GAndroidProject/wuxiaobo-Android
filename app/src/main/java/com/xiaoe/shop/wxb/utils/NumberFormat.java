package com.xiaoe.shop.wxb.utils;

import android.content.Context;

import com.xiaoe.shop.wxb.R;

public class NumberFormat {

    /**
     * 将数字格式化为 10w
     * @return
     */
    public static String viewCountToString(Context context, int count) {
        if(count < 10000){
            return ""+count;
        }
        float tempCount = count / (10000 * 1.0f);
        float strCount = (float)(Math.round(tempCount*10*2))/(10*2);
        return strCount + context.getString(R.string.ten_thousand);
    }

}

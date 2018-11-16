package com.xiaoe.shop.wxb.utils;

public class NumberFormat {

    /**
     * 将数字格式化为 10w
     * @return
     */
    public static String viewCountToString(int count){
        if(count < 10000){
            return ""+count;
        }
        float tempCount = count / (10000 * 1.0f);
        float strCount = (float)(Math.round(tempCount*10*2))/(10*2);
        return strCount+"W";
    }

}

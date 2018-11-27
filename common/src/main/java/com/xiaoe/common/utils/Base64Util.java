package com.xiaoe.common.utils;

import android.util.Base64;

import com.alibaba.fastjson.JSONObject;

public class Base64Util {
    public static JSONObject base64ToJSON(String base64){
        //base64解码
        String strJson = new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
        return JSONObject.parseObject(strJson);
    }
    public static String jsonToBase64(JSONObject jsonObject){
        //base64编码
        String str = jsonObject.toJSONString();
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }
}

package com.xiaoe.shop.wxb.business.upgrade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Deprecated
public class Result {
    public boolean status;
    public int code;
    public String message;

    public Result() {
    }

    public Result(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static <T> T parseResult(String json, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, clazz);
    }
}

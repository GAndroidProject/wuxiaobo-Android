package com.xiaoe.shop.wxb.business.upgrade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * @author flynnWang
 * @date 2018/10/17
 */
public class BaseResult {

    public boolean status;
    public int code;
    @SerializedName("message")
    public String msg;

    public BaseResult() {
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> T parseResult(String json, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, clazz);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

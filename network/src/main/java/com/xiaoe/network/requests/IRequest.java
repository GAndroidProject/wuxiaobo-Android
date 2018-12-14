package com.xiaoe.network.requests;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.entitys.DeviceInfo;
import com.xiaoe.network.network_interface.IBizCallback;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/25.
 */

public abstract class IRequest {


    private String cmd = "";
    protected DeviceInfo clientInfo = Global.g().getDeviceInfo();
    protected String buildVersion = Global.g().getVersionName();

    private Map<String,Object> formBody = new HashMap<String,Object>();

    private JSONObject buzDataParams = null;

    private JSONObject dataParams = null;

    private JSONObject bizDataParams = null;

    private IBizCallback iBizCallback;

    private boolean needCache = false;//是否需要缓存数据

    private String cacheKey;

    private String requestTag = "";//请求类型标识

    IRequest(String cmd, Class entityClass, IBizCallback iBizCallback){
        this.cmd = cmd;
        this.entityClass = entityClass;
        this.iBizCallback = iBizCallback;
    }
    IRequest(String cmd, IBizCallback iBizCallback){
        this.cmd = cmd;
        this.entityClass = null;
        this.iBizCallback = iBizCallback;
    }

    public String getCmd(){
        return cmd;
    }
    /**
     * 添加请求参数
     * @param key
     * @param value
     * @return
     */
    public boolean addRequestParam(String key,Object value){
        if(formBody.containsKey(key)){
           return false;
        }
        formBody.put(key,value);
        return true;
    }

    /**
     * 添加请求参数
     * @param form
     */
    public void addRequestParam(Map<String,String> form){
        formBody.putAll(form);
    }

    public boolean removeParam(String key){
        if(formBody.containsKey(key)){
            return false;
        }
        formBody.remove(key);
        return true;
    }

    public void addBUZDataParam(String key, Object val){
        if(buzDataParams == null){
            buzDataParams = new JSONObject();
        }
        buzDataParams.put(key, val);
    }

    public void addDataParam(String key, Object val){
        if(dataParams == null){
            dataParams = new JSONObject();
        }
        dataParams.put(key, val);
    }

    public void addBIZDataParam(String key, Object val){
        if (bizDataParams == null){
            bizDataParams = new JSONObject();
        }
        bizDataParams.put(key, val);
    }

    public Map<String, Object> getFormBody() {
        return formBody;
    }

    public String getUrlWithParams(String url) {
        StringBuilder builder = new StringBuilder(url);
        try {
            if (formBody.size() > 0) {
                builder.append("?");
            }
            int pos = 0;
            for (String key : formBody.keySet()) {
                if (pos > 0) {
                    builder.append("&");
                }
                builder.append(String.format("%s=%s", key, URLEncoder.encode(formBody.get(key).toString(), "utf-8")));
                pos++;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, "getUrlWithParams: " + e.getMessage());
        }
        return builder.toString();
    }

    public String getWrapedFormBody() {
        //暂时直接new对象，后期通过缓存获取
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("client","6");
        jsonObject.put("app_version","1.0");
        jsonObject.put("build_version",buildVersion);
        jsonObject.put("client_info",clientInfo);

        for(Map.Entry<String ,Object> entry : formBody.entrySet() ){
            jsonObject.put(entry.getKey(),entry.getValue());
        }
        if(buzDataParams != null){
            jsonObject.put("buz_data", buzDataParams);
        }
        if(dataParams != null){
            jsonObject.put("data", dataParams);
        }
        if(bizDataParams != null){
            jsonObject.put("biz_data", bizDataParams);
        }
        if(Constants.ANONYMOUS_USER_ID.equals(CommonUserInfo.getUserId())){
            //游客模式需要传一个user_id = "u_app_anonymous"
            jsonObject.put("user_id", Constants.ANONYMOUS_USER_ID);
        }else{
            jsonObject.put("user_id", CommonUserInfo.getUserId());
        }
        if (TextUtils.isEmpty(CommonUserInfo.getApiToken())) { // 若未有 api_token，则去数据库中取
            jsonObject.put("api_token", CommonUserInfo.getInstance().getApiTokenByDB());
        } else {
            jsonObject.put("api_token", CommonUserInfo.getApiToken());
        }
        jsonObject.put("shop_id", Constants.getAppId());
        jsonObject.put("app_id", Constants.getAppId());
        //设备唯一标识
        jsonObject.put("uuid", Global.g().getLocalMacAddressFromIp(XiaoeApplication.getmContext()));
        return jsonObject.toJSONString();
    }

    private boolean isPOST = true;

    public boolean isPOST() {
        return isPOST;
    }

    public void setPOST(boolean POST) {
        isPOST = POST;
    }

//    public Map<String, String> getHeader() {
//        return header;
//    }

    public void onResponse(boolean success, Object entityObj){
        iBizCallback.onResponse(this,success,entityObj);
    }
    private Class entityClass;

    public Class getEntityClass(){
        return this.entityClass;
    }

    public JSONObject getBuzDataParams() {
        return buzDataParams;
    }

    public JSONObject getDataParams() {
        return dataParams;
    }

    public JSONObject getBizDataParams() {
        return bizDataParams;
    }

    public boolean isNeedCache() {
        return needCache;
    }

    public void setNeedCache(boolean needCache) {
        this.needCache = needCache;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getRequestTag() {
        return requestTag;
    }

    public void setRequestTag(String requestTag) {
        this.requestTag = requestTag;
    }
}

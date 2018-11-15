package xiaoe.com.network.requests;

import com.alibaba.fastjson.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.app.Constants;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.DeviceInfo;
import xiaoe.com.network.network_interface.IBizCallback;

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

//    private Map<String, String> header;

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
        if(formBody.containsKey("app_id")){
            jsonObject.put("app_id", formBody.get("app_id"));
        }else {
            jsonObject.put("app_id", Constants.getAppId());
        }
        jsonObject.put("client","6");
//        jsonObject.put("device")
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
        if("0".equals(CommonUserInfo.getUserId())){
            //游客模式需要传一个user_id = "0"
            jsonObject.put("user_id", "0");
        }else{
            jsonObject.remove("user_id");
        }
        jsonObject.put("api_token", CommonUserInfo.getApiToken());
        jsonObject.put("shop_id", CommonUserInfo.getShopId());
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
}

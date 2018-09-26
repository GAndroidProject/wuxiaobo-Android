package xiaoe.com.network.requests;


import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.UserInfo;
import xiaoe.com.network.network_interface.IBizCallback;

/**
 * Created by Administrator on 2017/5/25.
 */

public abstract class IRequest {


    private String cmd = "";
    protected String clientInfo = Global.g().getDeviceInfo();
    protected String buildVersion = "1";
    public String getCmd(){
        return cmd;
    }

    private IBizCallback iBizCallback;

    IRequest(String cmd, Class entityClass, IBizCallback iBizCallback){
        this.cmd = cmd;
        this.entityClass = entityClass;
        this.iBizCallback = iBizCallback;
    }

    private Map<String,Object> formBody = new HashMap<String,Object>();

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

    public Map<String, Object> getFormBody() {
        return formBody;
    }
    public String getWrapedFormBody() {
        //暂时直接new对象，后期通过缓存获取
        UserInfo userInfo = new UserInfo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id",formBody.containsKey("app_id")?formBody.get("app_id"):"");
        jsonObject.put("client","6");
        jsonObject.put("app_version","1.0");
        jsonObject.put("build_version",buildVersion);
        jsonObject.put("client_info",clientInfo);

        String userId = "";
        String encryptData = "";
        if(userInfo != null){
            userId = userInfo.getUserId();
            encryptData = userInfo.getEncryptData();
        }
        jsonObject.put("user_id",userId);
        jsonObject.put("encrypt_data",encryptData);
        JSONObject buzDataJson = new JSONObject();
        for(Map.Entry<String ,Object> entry : formBody.entrySet() ){
            if("app_id".equals(entry.getKey())){
                continue;
            }
            buzDataJson.put(entry.getKey(),entry.getValue());
        }
        jsonObject.put("data",buzDataJson);
        return jsonObject.toJSONString();
    }

    private boolean isPOST = true;

    public boolean isPOST() {
        return isPOST;
    }

    public void setPOST(boolean POST) {
        isPOST = POST;
    }

    public void onResponse(boolean success, Object entityObj){
        iBizCallback.onResponse(this,success,entityObj);
    }
    private Class entityClass;

    public Class getEntityClass(){
        return this.entityClass;
    }
}

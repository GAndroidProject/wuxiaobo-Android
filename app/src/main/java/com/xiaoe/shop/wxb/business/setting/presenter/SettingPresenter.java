package com.xiaoe.shop.wxb.business.setting.presenter;

import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.GetPushStateRequest;
import com.xiaoe.network.requests.GetUnreadMessageRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SetPushStateRequest;
import com.xiaoe.network.requests.SettingPersonItemRequest;
import com.xiaoe.network.requests.SettingPseronMsgRequest;

import cn.jpush.android.api.JPushInterface;

public class SettingPresenter implements IBizCallback {

    private INetworkResponse inr;

    public SettingPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 获取个人信息
     * @param apiToken 登录成功后的 token
     * @param needAllMsg 是否需要全部信息，true -- 拿到全部信息，否则拿到微信昵称和头像
     */
    public void requestPersonData(String apiToken, boolean needAllMsg) {
        SettingPseronMsgRequest settingPseronMsgRequest = new SettingPseronMsgRequest(this);

        String msgType = needAllMsg ? "2" : "1";
//        settingPseronMsgRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPseronMsgRequest.addRequestParam("api_token", apiToken);
        settingPseronMsgRequest.addRequestParam("message_type", msgType);

        NetworkEngine.getInstance().sendRequest(settingPseronMsgRequest);
    }

    // 修改微信昵称
    public void updateWxNickname(String apiToken, String wxNickname) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest(this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("wx_nickname", wxNickname);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 修改真实姓名
    public void updateName(String apiToken, String wxName) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest( this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("wx_name", wxName);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新生日
    public void updateBirth(String apiToken, String birth) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest( this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("birth", birth);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新性别
    public void updateGender(String apiToken, String gender) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest(this);

        String wxGender = gender.equals("男") ? "1" : "2";
//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("wx_gender", wxGender);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新地址
    public void updateAddress(String apiToken, String address) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest( this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("address", address);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新职位
    public void updateJob(String apiToken, String job) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest( this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("job", job);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新公司
    public void updateCompany(String apiToken, String company) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest(this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("api_token", apiToken);
        settingPersonItemRequest.addRequestParam("company", company);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    // 更新手机号
    public void updatePhone(String apiToken, String smsCode, String newPhone) {
        SettingPersonItemRequest settingPersonItemRequest = new SettingPersonItemRequest(this);

//        settingPersonItemRequest.addHeaderParam("app-id", Constants.getWXAppId());
        settingPersonItemRequest.addRequestParam("apiToken", apiToken);
        settingPersonItemRequest.addRequestParam("sms_code", smsCode);
        settingPersonItemRequest.addRequestParam("new_phone", newPhone);

        NetworkEngine.getInstance().sendRequest(settingPersonItemRequest);
    }

    /**
     * 获取用户设置的消息状态
     */
    public void getPushState() {
        GetPushStateRequest request = new GetPushStateRequest(this);
        request.sendRequest();
    }

    /**
     * 设置用户设置的消息状态
     */
    public void setPushState(int state) {
        SetPushStateRequest request = new SetPushStateRequest(this);

        // 极光推送的 注册 ID
        request.addRequestParam("device_token", JPushInterface.getRegistrationID(XiaoeApplication.getmContext()));
        // 1-开启，2-关闭
        request.addRequestParam("is_push_state", state);

        request.sendRequest();
    }

    /**
     * 获取未读消息
     */
    public void requestUnreadMessage() {
        GetUnreadMessageRequest getUnreadMessageRequest = new GetUnreadMessageRequest(this);
        getUnreadMessageRequest.sendRequest();
    }

}

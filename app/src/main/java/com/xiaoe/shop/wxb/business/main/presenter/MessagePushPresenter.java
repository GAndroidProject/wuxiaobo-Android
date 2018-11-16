package com.xiaoe.shop.wxb.business.main.presenter;

import cn.jpush.android.api.JPushInterface;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.BindJgPushRequest;
import com.xiaoe.network.requests.IRequest;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：消息推送
 */
public class MessagePushPresenter implements IBizCallback {

    private static final String TAG = "MessagePushPresenter";

    private INetworkResponse inr;

    public MessagePushPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestBindJgPush(boolean isFormalUser) {
        BindJgPushRequest bindJgPushRequest = new BindJgPushRequest(this);

        // 店铺 ID
        bindJgPushRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        // 用户 ID
        bindJgPushRequest.addRequestParam("user_id", isFormalUser ? CommonUserInfo.getUserId() : "");
        // 极光推送的 注册 ID
        bindJgPushRequest.addRequestParam("device_token", JPushInterface.getRegistrationID(XiaoeApplication.getmContext()));
        // 客户端类型 - 1：Android    2：iOS
        bindJgPushRequest.addRequestParam("type", 1);

        bindJgPushRequest.sendRequest();
    }
}

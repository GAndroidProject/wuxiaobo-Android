package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：获取用户设置的消息状态（极光推送）
 */
public class GetPushStateRequest extends IRequest {

    public GetPushStateRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.BIND_JG_PUSH_URL + "xe.get.user.message.state/1.0.0", iBizCallback);
    }

    public void sendRequest() {
        NetworkEngine.getInstance().sendRequest(this);
    }
}

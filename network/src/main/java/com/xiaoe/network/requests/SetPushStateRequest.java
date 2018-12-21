package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：设置用户设置的消息状态（极光推送）
 */
public class SetPushStateRequest extends IRequest {

    public SetPushStateRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "xe.set.user.message.state/1.0.0", iBizCallback);
    }
}

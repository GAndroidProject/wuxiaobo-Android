package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：用户登录及绑定注册（极光推送）
 */
public class BindJgPushRequest extends IRequest {

    public BindJgPushRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "xe.set.device.token/1.0.0", iBizCallback);
    }

    public void sendRequest() {
        NetworkEngine.getInstance().sendRequest(this);
    }
}

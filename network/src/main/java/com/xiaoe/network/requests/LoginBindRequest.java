package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 绑定手机请求
public class LoginBindRequest extends IRequest {

    public LoginBindRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "auth/bind", iBizCallback);
    }
}

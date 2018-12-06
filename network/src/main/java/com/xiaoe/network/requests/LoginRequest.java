package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 微信登录请求
public class LoginRequest extends IRequest {

    public LoginRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "auth/login", null, iBizCallback);
    }
}

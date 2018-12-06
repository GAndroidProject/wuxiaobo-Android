package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 执行注册请求
public class LoginDoRegisterRequest extends IRequest {
    public LoginDoRegisterRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "auth/register", iBizCallback);
    }
}

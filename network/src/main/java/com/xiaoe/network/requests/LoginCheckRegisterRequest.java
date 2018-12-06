package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 检测首页注册请求
public class LoginCheckRegisterRequest extends IRequest {

    public LoginCheckRegisterRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "check_phone", iBizCallback);
    }
}

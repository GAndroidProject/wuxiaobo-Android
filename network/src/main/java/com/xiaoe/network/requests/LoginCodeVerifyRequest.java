package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 更换手机旧号码验证码确认请求
public class LoginCodeVerifyRequest extends IRequest {

    public LoginCodeVerifyRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "verify_code", iBizCallback);
    }
}

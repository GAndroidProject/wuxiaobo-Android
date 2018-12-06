package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 确认验证码请求（注册页面发送的请求）
public class LoginRegisterCodeVerifyRequest extends IRequest {

    public LoginRegisterCodeVerifyRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "verify_code", iBizCallback);
    }
}

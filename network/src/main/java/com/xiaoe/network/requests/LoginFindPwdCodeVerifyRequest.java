package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 找回密码验证码请求
public class LoginFindPwdCodeVerifyRequest extends IRequest {

    public LoginFindPwdCodeVerifyRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "verify_code", iBizCallback);
    }
}

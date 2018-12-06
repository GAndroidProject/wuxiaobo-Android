package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 手机验证码请求
public class LoginPhoneCodeRequest extends IRequest {
    public LoginPhoneCodeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "send_msg", iBizCallback);
    }
}

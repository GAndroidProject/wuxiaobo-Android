package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

// 手机验证码请求
public class LoginPhoneCodeRequest extends IRequest {
    public LoginPhoneCodeRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

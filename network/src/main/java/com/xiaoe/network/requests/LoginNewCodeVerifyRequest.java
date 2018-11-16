package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

// 更换手机新号码验证码确认请求
public class LoginNewCodeVerifyRequest extends IRequest {

    public LoginNewCodeVerifyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

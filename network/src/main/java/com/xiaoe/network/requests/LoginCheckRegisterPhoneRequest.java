package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

// 检查注册面请求
public class LoginCheckRegisterPhoneRequest extends IRequest {

    LoginCheckRegisterPhoneRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

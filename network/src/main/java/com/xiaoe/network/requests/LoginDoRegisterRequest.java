package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

// 执行注册请求
public class LoginDoRegisterRequest extends IRequest {
    public LoginDoRegisterRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

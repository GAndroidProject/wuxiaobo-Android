package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

// 绑定手机请求
public class LoginBindRequest extends IRequest {

    public LoginBindRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

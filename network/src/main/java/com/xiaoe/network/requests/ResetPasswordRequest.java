package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 重置密码请求
public class ResetPasswordRequest extends IRequest {

    public ResetPasswordRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "auth/reset_password", iBizCallback);
    }
}

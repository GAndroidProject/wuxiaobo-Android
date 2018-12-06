package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 更新手机号请求
public class UpdatePhoneRequest extends IRequest {

    public UpdatePhoneRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "reset_phone", iBizCallback);
    }
}

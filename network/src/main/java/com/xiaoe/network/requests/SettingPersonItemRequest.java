package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 修改个人信息请求
public class SettingPersonItemRequest extends IRequest {

    public SettingPersonItemRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "update_user_info", iBizCallback);
    }
}

package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

@Deprecated
public class GetVersionRequest extends IRequest {
    /**
     * 获取版本号
     */
    public GetVersionRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "msg/get_version", iBizCallback);
    }
}

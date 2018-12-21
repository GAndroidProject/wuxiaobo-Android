package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

@Deprecated
public class ContentRequest extends IRequest {
    /**
     * 获取专栏后内容（弃用，使用DetailRequest）
     */
    public ContentRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.resource.content.get/1.0.0", iBizCallback);
    }
}

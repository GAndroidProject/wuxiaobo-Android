package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * 可充值列表请求
 * @author: zak
 * @date: 2019/1/4
 */
public class TopUpListRequest extends IRequest {

    public TopUpListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.iap.product/1.0.0", iBizCallback);
    }
}

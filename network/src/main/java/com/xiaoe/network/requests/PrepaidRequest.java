package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * 波币充值预支付下单信息
 * @author: zak
 * @date: 2019/1/4
 */
public class PrepaidRequest extends IRequest {

    public PrepaidRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL +"xe.iap.place.order/1.0.0", iBizCallback);
    }
}

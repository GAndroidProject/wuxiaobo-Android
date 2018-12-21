package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class PayOrderRequest extends IRequest {

    public PayOrderRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.order.place/1.0.0", iBizCallback);
    }
}

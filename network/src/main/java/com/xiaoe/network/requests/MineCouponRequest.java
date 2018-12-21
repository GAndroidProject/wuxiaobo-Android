package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class MineCouponRequest extends IRequest {

    public MineCouponRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.get/1.0.0", iBizCallback);
    }
}

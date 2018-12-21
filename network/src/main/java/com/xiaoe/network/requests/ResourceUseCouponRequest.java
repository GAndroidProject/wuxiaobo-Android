package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class ResourceUseCouponRequest extends IRequest {

    public ResourceUseCouponRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.coupon.get/1.0.0", iBizCallback);
    }
}

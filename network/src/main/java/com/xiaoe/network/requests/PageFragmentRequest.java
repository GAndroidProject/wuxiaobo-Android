package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class PageFragmentRequest extends IRequest {

    public PageFragmentRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.shop.page.get/1.0.0", iBizCallback);
    }
}

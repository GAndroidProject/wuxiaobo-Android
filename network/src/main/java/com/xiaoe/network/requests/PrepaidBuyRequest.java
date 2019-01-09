package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author: zak
 * @date: 2019/1/4
 */
public class PrepaidBuyRequest extends IRequest {

    public PrepaidBuyRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.balance.charge/1.0.0", iBizCallback);
    }
}

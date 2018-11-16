package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class EarningRequest extends IRequest {

    public EarningRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.API_THIRD_BASE_URL + "get_c_balance_flow", iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.asset.get/1.0.0", iBizCallback);
    }
}

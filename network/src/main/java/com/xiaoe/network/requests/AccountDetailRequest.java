package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author: zak
 * @date: 2019/1/10
 */
public class AccountDetailRequest extends IRequest {

    public AccountDetailRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.iap.water.list/1.0.0", iBizCallback);
    }
}

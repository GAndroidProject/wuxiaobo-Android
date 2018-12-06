package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 提现请求
public class WithdrawRequest extends IRequest {

    public WithdrawRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.asset.withdraw/1.0.0", iBizCallback);
    }
}

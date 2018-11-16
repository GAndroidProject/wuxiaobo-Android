package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 奖学金领取请求
public class ScholarshipReceiveRequest extends IRequest {

    public ScholarshipReceiveRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.task.result/1.0.0", iBizCallback);
    }
}

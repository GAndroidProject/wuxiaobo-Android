package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 奖学金提交请求
public class ScholarshipSubmitRequest extends IRequest {

    public ScholarshipSubmitRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.submit.task/1.0.0", iBizCallback);
    }
}

package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

@Deprecated
// 图文详情请求
public class CourseITDetailRequest extends IRequest {

    public CourseITDetailRequest( IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.detail.get/1.0.0", iBizCallback);
    }
}

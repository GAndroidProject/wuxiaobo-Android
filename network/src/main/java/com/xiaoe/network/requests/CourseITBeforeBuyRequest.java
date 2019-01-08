package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

@Deprecated
public class CourseITBeforeBuyRequest extends IRequest {

    CourseITBeforeBuyRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CourseITBeforeBuyRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.info.get/1.0.0", iBizCallback);
    }
}

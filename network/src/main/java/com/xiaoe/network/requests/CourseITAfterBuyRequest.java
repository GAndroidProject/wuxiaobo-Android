package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class CourseITAfterBuyRequest extends IRequest {

    CourseITAfterBuyRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CourseITAfterBuyRequest( IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.resource.content.get/1.0.0", iBizCallback);
    }
}

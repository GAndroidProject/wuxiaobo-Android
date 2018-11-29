package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class ScholarshipBoughtListRequest extends IRequest {

    public ScholarshipBoughtListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.share.list/1.0.0",  iBizCallback);
    }
}

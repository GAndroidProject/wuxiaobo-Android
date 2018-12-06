package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class SearchRequest extends IRequest {

    public SearchRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public SearchRequest(String cmd, IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + cmd, iBizCallback);
    }
}

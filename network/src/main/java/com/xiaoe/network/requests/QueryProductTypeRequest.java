package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class QueryProductTypeRequest extends IRequest {

    public QueryProductTypeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL+"xe.get.product_type/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

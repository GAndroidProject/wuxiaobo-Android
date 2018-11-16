package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

public class MineRequest extends IRequest {

    public MineRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

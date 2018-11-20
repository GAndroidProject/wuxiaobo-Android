package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

public class UnReadMsgRequest extends IRequest {

    public UnReadMsgRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

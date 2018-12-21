package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class CommentDeleteRequest extends IRequest {

    public CommentDeleteRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.comments.del/1.0.0", iBizCallback);
    }
}

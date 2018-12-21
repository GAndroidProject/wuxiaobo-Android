package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class CommentLikeRequest extends IRequest {

    public CommentLikeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.comments.like.add/1.0.0", iBizCallback);
    }
}

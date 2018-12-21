package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * 移除收藏
 */
public class RemoveCollectionListRequest extends IRequest {

    public RemoveCollectionListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.favorites.del/1.0.0", iBizCallback);
    }
}

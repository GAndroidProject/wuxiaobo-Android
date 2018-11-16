package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

// 删除指定收藏接口
public class RemoveCollectioinByObjRequest extends IRequest {

    public RemoveCollectioinByObjRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public RemoveCollectioinByObjRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.favorites.multi.del/1.0.0",  iBizCallback);
    }
}

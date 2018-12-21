package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * 拉取专栏的资源列表
 */
public class ColumnListRequst extends IRequest {

    public ColumnListRequst(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.relation.get/1.0.0", iBizCallback);
    }
}

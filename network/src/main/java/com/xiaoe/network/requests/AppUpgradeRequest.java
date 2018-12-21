package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class AppUpgradeRequest extends IRequest {
    /**
     * 检查更新
     */
    public AppUpgradeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "xe.check.version/1.0.0", iBizCallback);
    }
}

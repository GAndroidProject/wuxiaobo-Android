package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/12/21
 * <p>
 * 描述：发布版本（提示版本更新）
 */
public class ReleaseVersionRequest extends IRequest {

    ReleaseVersionRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "xe.set.version/1.0.0", iBizCallback);
    }
}

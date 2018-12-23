package com.xiaoe.network.requests;

import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/12/21
 * <p>
 * 描述：发布版本（提示版本更新）z
 */
public class ReleaseVersionRequest extends IRequest {

    public ReleaseVersionRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd + "xe.set.version/1.0.0", iBizCallback);
    }
}

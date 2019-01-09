package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author: zak
 * @date: 2019/1/3
 */
public class DownloadListRequest extends IRequest {

    public DownloadListRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.resource.content.download/1.0.0", iBizCallback);
    }
}

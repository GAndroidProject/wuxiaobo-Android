package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：获取消息未读数
 */
public class GetUnreadMessageRequest extends IRequest {

    public GetUnreadMessageRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.message_count.get/1.0.0", iBizCallback);
    }
}

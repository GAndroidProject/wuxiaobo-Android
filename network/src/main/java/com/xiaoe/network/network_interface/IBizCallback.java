package com.xiaoe.network.network_interface;


import com.xiaoe.network.requests.IRequest;

/**
 * Created by Administrator on 2017/5/25.
 */

public interface IBizCallback {
    void onResponse(IRequest iRequest, boolean success, Object entity);
}

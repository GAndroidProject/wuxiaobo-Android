package com.xiaoe.shop.wxb.business.launch.presenter;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.UnReadMsgRequest;

public class UnReadMsgPresenter implements IBizCallback {

    private INetworkResponse inr;

    public UnReadMsgPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestUnReadCouponMsg() {
        UnReadMsgRequest unReadMsgRequest = new UnReadMsgRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.unread/1.0.0", this);

        NetworkEngine.getInstance().sendRequest(unReadMsgRequest);
    }

}

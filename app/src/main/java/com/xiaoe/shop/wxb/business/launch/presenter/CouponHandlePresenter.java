package com.xiaoe.shop.wxb.business.launch.presenter;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.CouponHandleRequest;

public class CouponHandlePresenter implements IBizCallback {

    private INetworkResponse inr;

    public CouponHandlePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 获取未读优惠券信息
    public void requestUnReadCouponMsg() {
        CouponHandleRequest couponHandleRequest = new CouponHandleRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.unread/1.0.0", this);

        NetworkEngine.getInstance().sendRequest(couponHandleRequest);
    }

    // 请求发送优惠券
    public void requestSendCoupon() {
        CouponHandleRequest couponHandleRequest = new CouponHandleRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.user.coupon.grant/1.0.0", this);

        NetworkEngine.getInstance().sendRequest(couponHandleRequest);
    }

}

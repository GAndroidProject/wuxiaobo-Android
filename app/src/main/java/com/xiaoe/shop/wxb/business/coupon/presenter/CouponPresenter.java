package com.xiaoe.shop.wxb.business.coupon.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CouponCanResourceRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.MineCouponRequest;
import com.xiaoe.network.requests.ResourceUseCouponRequest;

public class CouponPresenter implements IBizCallback {

    private INetworkResponse inr;

    public CouponPresenter(INetworkResponse inr) {
        this.inr = inr;
    }


    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 获取我的优惠券
     * all,valid,invalid,默认all, valid查询有效优惠券，invalid查询失效优惠券
     * @param type
     */
    public void requestMineCoupon(String type) {
        MineCouponRequest couponRequest = new MineCouponRequest(this);

        couponRequest.addRequestParam("type", type);
        couponRequest.sendRequest();
    }

    public void requestResourceUseCoupon(String resourceId, int price){
        ResourceUseCouponRequest useCouponRequest = new ResourceUseCouponRequest(this);

        useCouponRequest.addRequestParam("price", price);
        useCouponRequest.addRequestParam("resource_id", resourceId);
        useCouponRequest.sendRequest();
    }

    public void requestCouponCanResource(String couponId){
        CouponCanResourceRequest canResourceRequest = new CouponCanResourceRequest(this);

        canResourceRequest.addRequestParam("coupon_id", couponId);

        canResourceRequest.sendRequest();
    }
}

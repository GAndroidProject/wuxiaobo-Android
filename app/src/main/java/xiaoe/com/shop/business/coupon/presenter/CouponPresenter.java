package xiaoe.com.shop.business.coupon.presenter;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CouponCanResourceRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineCouponRequest;
import xiaoe.com.network.requests.ResourceUseCouponRequest;

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
        couponRequest.addRequestParam("app_id", Constants.getAppId());
        couponRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        couponRequest.addRequestParam("type", type);
        couponRequest.sendRequest();
    }

    public void requestResourceUseCoupon(String resourceId, int price){
        ResourceUseCouponRequest useCouponRequest = new ResourceUseCouponRequest(this);
        useCouponRequest.addRequestParam("app_id", Constants.getAppId());
        useCouponRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        useCouponRequest.addRequestParam("price", price);
        useCouponRequest.addRequestParam("resource_id", resourceId);
        useCouponRequest.sendRequest();
    }

    public void requestCouponCanResource(String couponId){
        CouponCanResourceRequest canResourceRequest = new CouponCanResourceRequest(this);
        canResourceRequest.addRequestParam("app_id", Constants.getAppId());
        canResourceRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        canResourceRequest.addRequestParam("coupon_id", couponId);

        canResourceRequest.sendRequest();
    }
}

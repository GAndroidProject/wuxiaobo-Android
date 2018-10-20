package xiaoe.com.shop.business.coupon.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CouponRequest;
import xiaoe.com.network.requests.IRequest;

public class CouponPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public CouponPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = ""; // 默认接口
    }

    public CouponPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        CouponRequest couponRequest = new CouponRequest(cmd, null, this);
        couponRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(couponRequest);
    }
}

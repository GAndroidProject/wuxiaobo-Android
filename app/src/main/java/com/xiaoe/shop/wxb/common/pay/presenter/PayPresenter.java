package com.xiaoe.shop.wxb.common.pay.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaoe.common.app.Constants;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.PayOrderRequest;
import com.xiaoe.network.requests.PaySuperVipRequest;
import com.xiaoe.network.requests.PrepaidBuyRequest;
import com.xiaoe.network.requests.PrepaidRequest;
import com.xiaoe.network.requests.TopUpListRequest;

public class PayPresenter implements IBizCallback {
    private static final String TAG = "PayPresenter";

    public static final int PAY_WAY_DEFAULT = 0; // 默认为微信支付
    public static final int PAY_WAY_ALI = 1;     // 支付宝支付
    public static final int PAY_WAY_VIRTUAL = 3; // 波豆支付

    private INetworkResponse iNetworkResponse;
    private IWXAPI wxapi;
    public PayPresenter(Context context, INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
        wxapi = WXAPIFactory.createWXAPI(context, Constants.getWXAppId(),true);
        wxapi.registerApp(Constants.getWXAppId());
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }

    /**
     * 下单支付
     * @param paymentType  付费类型
     * @param resourceType 资源类型
     * @param payWay       支付方式
     * @param resourceId   资源id
     * @param productId    购买超级会员时才需要传，其他资源使用 resourceId
     */
    public void payOrder(int paymentType, int resourceType, int payWay, String resourceId, String productId, String couponId){
        PayOrderRequest payOrderRequest = new PayOrderRequest(this);
        payOrderRequest.addBUZDataParam("user_account_type", "0");
        payOrderRequest.addBUZDataParam("force_collection", "0");
        payOrderRequest.addBUZDataParam("payment_type", ""+paymentType);
        payOrderRequest.addBUZDataParam("resource_type", ""+resourceType);
        payOrderRequest.addBUZDataParam("resource_id", ""+resourceId);
        payOrderRequest.addBUZDataParam("product_id", ""+productId);
        payOrderRequest.addBUZDataParam("pay_way", payWay);
        if(!TextUtils.isEmpty(couponId)){
            payOrderRequest.addBUZDataParam("cu_id", couponId);
        }
        payOrderRequest.sendRequest();
    }

    /**
     * 呼起微信支付
     */
    public void pullWXPay(String appId, String partnerId, String prepayId, String noncestr, String timestamp, String wxPay, String sign){
        if(TextUtils.isEmpty(wxPay)){
            wxPay = "Sign=WXPay";
        }
        PayReq req = new PayReq();
        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
        req.appId			= appId;
        req.partnerId		= partnerId;
        req.prepayId		= prepayId;
        req.nonceStr		= noncestr;
        req.timeStamp		= timestamp;
        req.packageValue	= wxPay;
        req.sign			= sign;
        wxapi.sendReq(req);
    }

    /**
     * 购买超级会员
     */
    public void paySuperVip() {
        PaySuperVipRequest paySuperVipRequest = new PaySuperVipRequest(this);

        paySuperVipRequest.sendRequest();
    }

    /**
     * 获取可充值列表
     */
    public void getTopUpList() {
        TopUpListRequest topUpListRequest = new TopUpListRequest(this);

        topUpListRequest.sendRequest();
    }

    /**
     * 波币预支付下单信息请求
     */
    public void prepaidMsg(String productId) {
        PrepaidRequest prepaidRequest = new PrepaidRequest(this);

        prepaidRequest.addRequestParam("product_id", productId);
        prepaidRequest.addRequestParam("wallet_type", 2); // 安卓波币充值默认值

        prepaidRequest.sendRequest();
    }

    /**
     * 波币第三方支付预支付信息
     */
    public void prepaidBuyMsg(String orderId, int payWay) {
        PrepaidBuyRequest prepaidBuyRequest = new PrepaidBuyRequest(this);

        prepaidBuyRequest.addRequestParam("pre_order_id", orderId);
        prepaidBuyRequest.addRequestParam("pay_way", payWay);

        prepaidBuyRequest.sendRequest();
    }
}

package xiaoe.com.shop.common.pay;

import android.content.Context;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PayOrderRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class PayPresenter implements IBizCallback {
    private static final String TAG = "PayPresenter";
    private INetworkResponse iNetworkResponse;
    private IWXAPI wxapi;
    public PayPresenter(Context context, INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
        wxapi = WXAPIFactory.createWXAPI(context, Constants.getWXAppId(),true);
        wxapi.registerApp(Constants.getWXAppId());
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                iNetworkResponse.onMainThreadResponse(iRequest, success, entity);
            }
        });
    }

    /**
     * 下单支付
     * @param paymentType
     * @param resourceType
     * @param resourceId
     * @param productId
     */
    public void payOrder(int paymentType, int resourceType, String resourceId, String productId){
        PayOrderRequest payOrderRequest = new PayOrderRequest(this);
        payOrderRequest.addBUZDataParam("user_account_type", "0");
        payOrderRequest.addBUZDataParam("force_collection", "1");
        payOrderRequest.addBUZDataParam("payment_type", ""+paymentType);
        payOrderRequest.addBUZDataParam("resource_type", ""+resourceType);
        payOrderRequest.addBUZDataParam("resource_id", ""+resourceId);
        payOrderRequest.addBUZDataParam("product_id", ""+productId);
        payOrderRequest.addRequestParam("app_id", "apppcHqlTPT3482");
        payOrderRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        payOrderRequest.sendRequest();
    }

    /**
     * 呼起微信支付
     */
    public void pullWXPay(String appId, String partnerId, String prepayId, String noncestr, String timestamp, String wxPay, String sign){
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
}

package xiaoe.com.shop.common.pay;

import android.content.Context;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;

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
    public void onResponse(IRequest iRequest, boolean success, Object entity) {

    }


    public void pullWXPay(){
        PayReq req = new PayReq();
        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//        req.appId			= payEntity.getAppid();
//        req.partnerId		= payEntity.getPartnerid();
//        req.prepayId		= payEntity.getPrepayid();
//        req.nonceStr		= payEntity.getNoncestr();
//        req.timeStamp		= payEntity.getTimestamp();
//        req.packageValue	= payEntity.getWXPay();
//        req.sign			= payEntity.getSign();
        wxapi.sendReq(req);
    }
}

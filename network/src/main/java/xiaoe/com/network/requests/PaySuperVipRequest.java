package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 超级会员下单请求
public class PaySuperVipRequest extends IRequest {

    public PaySuperVipRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.svip.pay.info.get/1.0.0", iBizCallback);
    }
}

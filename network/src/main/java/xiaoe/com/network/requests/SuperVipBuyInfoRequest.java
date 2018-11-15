package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 超级会员购买请求
public class SuperVipBuyInfoRequest extends IRequest {

    public SuperVipBuyInfoRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.svip.pay.info.get/1.0.0", iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 提现请求
public class WithDrawalRequest extends IRequest {

    public WithDrawalRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.asset.withdraw/1.0.0", iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 超级会员下单请求
public class PaySuperVipRequest extends IRequest {

    public PaySuperVipRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

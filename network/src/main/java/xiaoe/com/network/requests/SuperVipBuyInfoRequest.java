package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 超级会员购买请求
public class SuperVipBuyInfoRequest extends IRequest {

    public SuperVipBuyInfoRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 是否为超级会员的请求
public class IsSuperVipRequest extends IRequest {

    public IsSuperVipRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

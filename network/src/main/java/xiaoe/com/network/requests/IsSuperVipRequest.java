package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 是否为超级会员的请求
public class IsSuperVipRequest extends IRequest {

    public IsSuperVipRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.svip.info.get/1.0.0", iBizCallback);
    }
}

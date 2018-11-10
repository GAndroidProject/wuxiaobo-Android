package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 游客登录店铺 id 请求
public class TouristsShopIdRequest extends IRequest {

    public TouristsShopIdRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

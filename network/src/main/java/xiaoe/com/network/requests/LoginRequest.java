package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 微信登录请求
public class LoginRequest extends IRequest {

    public LoginRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, null, iBizCallback);
    }
}

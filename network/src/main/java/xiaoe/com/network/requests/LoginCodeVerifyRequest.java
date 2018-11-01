package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 登录验证码确认请求
public class LoginCodeVerifyRequest extends IRequest {

    public LoginCodeVerifyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

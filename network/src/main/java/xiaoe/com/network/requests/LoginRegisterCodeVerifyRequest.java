package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 确认验证码请求（注册页面发送的请求）
public class LoginRegisterCodeVerifyRequest extends IRequest {

    public LoginRegisterCodeVerifyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

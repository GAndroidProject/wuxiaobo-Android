package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 找回密码验证码请求
public class LoginFindPwdCodeVerifyRequest extends IRequest {

    public LoginFindPwdCodeVerifyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

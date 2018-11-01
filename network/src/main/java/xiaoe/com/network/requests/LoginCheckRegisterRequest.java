package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 检测首页注册请求
public class LoginCheckRegisterRequest extends IRequest {

    public LoginCheckRegisterRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

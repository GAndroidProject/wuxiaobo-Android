package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 绑定手机请求
public class LoginBindRequest extends IRequest {

    public LoginBindRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

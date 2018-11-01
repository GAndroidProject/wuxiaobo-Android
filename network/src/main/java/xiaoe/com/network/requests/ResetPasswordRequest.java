package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 重置密码请求
public class ResetPasswordRequest extends IRequest {

    public ResetPasswordRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

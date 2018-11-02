package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 更新手机号请求
public class UpdatePhoneRequest extends IRequest {

    public UpdatePhoneRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

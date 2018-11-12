package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 提现请求
public class WithDrawalRequest extends IRequest {

    public WithDrawalRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

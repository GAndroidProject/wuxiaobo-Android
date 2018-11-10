package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 积分详情请求
public class IntegralRequest extends IRequest {

    public IntegralRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

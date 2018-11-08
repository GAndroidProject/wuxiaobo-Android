package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 奖学金领取请求
public class ScholarshipReceiveRequest extends IRequest {

    public ScholarshipReceiveRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

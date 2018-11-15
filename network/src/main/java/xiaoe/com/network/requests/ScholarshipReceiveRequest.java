package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 奖学金领取请求
public class ScholarshipReceiveRequest extends IRequest {

    public ScholarshipReceiveRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.task.status/1.0.0", iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 更新学习记录接口
public class UpdateMineLearningRequest extends IRequest {

    public UpdateMineLearningRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.learning.records.push/1.0.0", iBizCallback);
    }
}

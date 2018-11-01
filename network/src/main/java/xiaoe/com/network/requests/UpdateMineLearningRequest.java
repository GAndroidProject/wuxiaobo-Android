package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 更新学习记录接口
public class UpdateMineLearningRequest extends IRequest {

    public UpdateMineLearningRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 奖学金提交请求
public class ScholarshipSubmitRequest extends IRequest {

    public ScholarshipSubmitRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

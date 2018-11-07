package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 任务状态请求
public class ScholarshipTaskStateRequest extends IRequest {

    public ScholarshipTaskStateRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

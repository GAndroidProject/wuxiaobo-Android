package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 图文详情请求
public class CourseITDetailRequest extends IRequest {

    public CourseITDetailRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

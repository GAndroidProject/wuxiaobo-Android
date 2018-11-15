package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 图文详情请求
public class CourseITDetailRequest extends IRequest {

    public CourseITDetailRequest( IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.detail.get/1.0.0", iBizCallback);
    }
}

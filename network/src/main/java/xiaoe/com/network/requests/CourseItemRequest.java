package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CourseItemRequest extends IRequest {

    public CourseItemRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, null, iBizCallback);
    }
}

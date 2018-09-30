package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CourseRequest extends IRequest {

    public CourseRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

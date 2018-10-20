package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CourseMoreRequest extends IRequest {

    public CourseMoreRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

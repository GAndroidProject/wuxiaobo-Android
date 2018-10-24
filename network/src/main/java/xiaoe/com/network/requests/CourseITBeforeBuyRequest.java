package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CourseITBeforeBuyRequest extends IRequest {

    CourseITBeforeBuyRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CourseITBeforeBuyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

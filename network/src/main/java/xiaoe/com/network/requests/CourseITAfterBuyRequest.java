package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CourseITAfterBuyRequest extends IRequest {

    CourseITAfterBuyRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CourseITAfterBuyRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

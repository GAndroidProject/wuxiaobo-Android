package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class HomepageRequest extends IRequest {

    public HomepageRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

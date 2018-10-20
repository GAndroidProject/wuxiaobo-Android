package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class NaviDetailRequest extends IRequest {

    public NaviDetailRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

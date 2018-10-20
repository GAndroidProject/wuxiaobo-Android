package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CouponRequest extends IRequest {

    public CouponRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

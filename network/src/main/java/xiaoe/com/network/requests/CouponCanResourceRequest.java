package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class CouponCanResourceRequest extends IRequest {
    public CouponCanResourceRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL+"xe.coupon.info/1.0.0", iBizCallback);
    }

    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

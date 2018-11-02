package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class MineCouponRequest extends IRequest {

    public MineCouponRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.PLY_BASE_URL+"api/xe.user.coupon.get/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

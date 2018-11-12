package xiaoe.com.network.requests;


import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class PayOrderRequest extends IRequest {
    public PayOrderRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.PLY_BASE_URL+"pay/get_info", iBizCallback);
//        super(NetworkEngine.API_BASE_URL+"xe.order.place/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

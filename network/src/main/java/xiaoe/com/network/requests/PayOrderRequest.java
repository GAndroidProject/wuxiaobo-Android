package xiaoe.com.network.requests;


import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class PayOrderRequest extends IRequest {
    public PayOrderRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.PLY_BASE_URL+"pay/get_info", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

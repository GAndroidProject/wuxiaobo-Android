package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class GetVersionRequest extends IRequest {

    /**
     * 获取版本号
     */
    public GetVersionRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.CLASS_DETAIL_BASE_URL + "msg/get_version", iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "msg/get_version", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

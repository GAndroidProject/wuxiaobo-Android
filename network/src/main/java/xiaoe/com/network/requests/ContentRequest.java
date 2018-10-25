package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class ContentRequest extends IRequest {

    /**
     * 获取专栏后内容
     */
    public ContentRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.resource.content.get/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class AppUpgradeRequest extends IRequest {

    /**
     * 检查更新
     */
    public AppUpgradeRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "msg/check_version", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequestByGet(this);
    }
}

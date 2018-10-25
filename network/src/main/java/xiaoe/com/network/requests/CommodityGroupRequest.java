package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CommodityGroupRequest extends IRequest {

    public CommodityGroupRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

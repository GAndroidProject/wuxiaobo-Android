package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class CommodityGroupRequest extends IRequest {

    public CommodityGroupRequest( Class entityClass, IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.goods.more.get/1.0.0", entityClass, iBizCallback);
    }
}

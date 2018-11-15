package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class CdKeyRequest extends IRequest {

    public CdKeyRequest(Class entityClass, IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.redeemcode.redeem/1.0.0", entityClass, iBizCallback);
    }
}

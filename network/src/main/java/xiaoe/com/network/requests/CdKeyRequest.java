package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CdKeyRequest extends IRequest {

    public CdKeyRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

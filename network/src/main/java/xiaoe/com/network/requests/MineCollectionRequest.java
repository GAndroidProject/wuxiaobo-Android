package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class MineCollectionRequest extends IRequest {

    public MineCollectionRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

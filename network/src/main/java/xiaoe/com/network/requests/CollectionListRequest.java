package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class CollectionListRequest extends IRequest {

    public CollectionListRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public CollectionListRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

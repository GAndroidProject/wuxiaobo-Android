package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class PageFragmentRequest extends IRequest {

    public PageFragmentRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

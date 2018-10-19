package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class LoginRequest extends IRequest {

    public LoginRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

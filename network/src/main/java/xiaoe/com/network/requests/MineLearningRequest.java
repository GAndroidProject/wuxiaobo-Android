package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class MineLearningRequest extends IRequest {

    public MineLearningRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}

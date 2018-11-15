package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class MineLearningRequest extends IRequest {

    public MineLearningRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.learning.records.get/1.0.0", null, iBizCallback);
    }
}

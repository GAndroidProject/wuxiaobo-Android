package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class GetHistoryMessageRequest extends IRequest {

    public GetHistoryMessageRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.user.message.get/1.0.0", iBizCallback);
    }

    public void sendRequest() {
        NetworkEngine.getInstance().sendRequest(this);
    }
}

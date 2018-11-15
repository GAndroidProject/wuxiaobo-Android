package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

/**
 * @author flynnWang
 * @date 2018/11/12
 * <p>
 * 描述：用户登录及绑定注册（极光推送）
 */
public class BindJgPushRequest extends IRequest {

    public BindJgPushRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.BIND_JG_PUSH_URL + "msg/set_device_token", iBizCallback);
//        super(NetworkEngine.API_THIRD_BASE_URL + "msg/set_device_token", iBizCallback);
    }

    public void sendRequest() {
        NetworkEngine.getInstance().sendRequest(this);
    }
}

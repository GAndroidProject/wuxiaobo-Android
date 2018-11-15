package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class SettingPseronMsgRequest extends IRequest {

    public SettingPseronMsgRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public SettingPseronMsgRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.LOGIN_BASE_URL + "get_person_message" , null, iBizCallback);
    }
}

package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class SettingPseronMsgRequest extends IRequest {

    public SettingPseronMsgRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }

    public SettingPseronMsgRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, null, iBizCallback);
    }
}

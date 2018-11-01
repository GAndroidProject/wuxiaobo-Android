package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

// 修改个人信息请求
public class SettingPersonItemRequest extends IRequest {

    public SettingPersonItemRequest(String cmd, IBizCallback iBizCallback) {
        super(cmd, iBizCallback);
    }
}

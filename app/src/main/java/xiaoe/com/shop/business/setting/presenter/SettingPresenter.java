package xiaoe.com.shop.business.setting.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SettingRequest;

public class SettingPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public SettingPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = ""; // 默认接口
    }

    public SettingPresenter(String cmd, INetworkResponse inr) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        SettingRequest settingRequest = new SettingRequest(cmd, null, this);
        settingRequest.addRequestParam("add_id", "12345");
        NetworkEngine.getInstance().sendRequest(settingRequest);
    }
}

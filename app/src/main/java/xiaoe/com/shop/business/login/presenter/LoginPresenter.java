package xiaoe.com.shop.business.login.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.LoginRequest;

public class LoginPresenter implements IBizCallback {

    private INetworkResponse inr = null;
    private String cmd;

    public LoginPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        LoginRequest loginRequest = new LoginRequest(cmd, null, this);
        loginRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(loginRequest);
    }
}

package xiaoe.com.shop.business.navigate_detail.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.NaviDetailRequest;

public class NavigateDetailPresenter implements IBizCallback {

    private INetworkResponse inr;
    String cmd;

    public NavigateDetailPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "";
    }

    public NavigateDetailPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        NaviDetailRequest naviDetailRequest = new NaviDetailRequest(cmd, null, this);
        naviDetailRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(naviDetailRequest);
    }
}

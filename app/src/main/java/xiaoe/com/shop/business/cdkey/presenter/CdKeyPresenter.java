package xiaoe.com.shop.business.cdkey.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CdKeyRequest;
import xiaoe.com.network.requests.IRequest;

public class CdKeyPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public CdKeyPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "";
    }

    public CdKeyPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        CdKeyRequest cdKeyRequest = new CdKeyRequest(cmd, null, this);
        cdKeyRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(cdKeyRequest);
    }
}

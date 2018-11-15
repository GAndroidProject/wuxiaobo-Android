package xiaoe.com.shop.business.cdkey.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CdKeyRequest;
import xiaoe.com.network.requests.IRequest;

public class CdKeyPresenter implements IBizCallback {

    private INetworkResponse inr;

    public CdKeyPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    public CdKeyPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData(String code) {
        CdKeyRequest cdKeyRequest = new CdKeyRequest(null, this);
        cdKeyRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        cdKeyRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        cdKeyRequest.addRequestParam("code", code);
        NetworkEngine.getInstance().sendRequest(cdKeyRequest);
    }
}

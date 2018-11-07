package xiaoe.com.shop.business.cdkey.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CdKeyRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class CdKeyPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public CdKeyPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "api/xe.redeem_code.redeem/1.0.0";
    }

    public CdKeyPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                inr.onMainThreadResponse(iRequest, success, entity);
            }
        });
    }

    public void requestData(String code) {
        CdKeyRequest cdKeyRequest = new CdKeyRequest(NetworkEngine.PLY_BASE_URL + cmd, null, this);
        cdKeyRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        cdKeyRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        cdKeyRequest.addRequestParam("code", code);
        NetworkEngine.getInstance().sendRequest(cdKeyRequest);
    }
}

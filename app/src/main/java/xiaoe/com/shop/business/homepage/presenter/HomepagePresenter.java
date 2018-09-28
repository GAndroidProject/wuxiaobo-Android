package xiaoe.com.shop.business.homepage.presenter;

import xiaoe.com.common.entitys.TestInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.HomepageRequest;
import xiaoe.com.network.requests.IRequest;

public class HomepagePresenter implements IBizCallback {

    private INetworkResponse inr;

    public HomepagePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onMainThreadResponse(iRequest, success, entity);
    }

    public void requestData() {
        HomepageRequest hp = new HomepageRequest("test/test_app", TestInfo.class, this);
        hp.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(hp);
    }
}

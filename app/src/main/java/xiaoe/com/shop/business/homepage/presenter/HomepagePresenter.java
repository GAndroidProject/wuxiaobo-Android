package xiaoe.com.shop.business.homepage.presenter;

import xiaoe.com.common.entitys.TestComponent;
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
        // test/test_app api/xe.shop.page.get/1.0.0
        HomepageRequest hp = new HomepageRequest("api/xe.shop.page.get/1.0.0", TestComponent.class, this);
        hp.addRequestParam("user_id", "u_5ad010f47073c_yeHaGL9bEG");
        hp.addRequestParam("shop_id", "apppcHqlTPT3482");
        hp.addRequestParam("micro_page_id", 0);
        NetworkEngine.getInstance().sendRequest(hp);
    }
}

package xiaoe.com.shop.business.mine.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineRequest;

public class MinePresenter implements IBizCallback {

    private INetworkResponse inr;

    public MinePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onMainThreadResponse(iRequest, success, entity);
    }

}

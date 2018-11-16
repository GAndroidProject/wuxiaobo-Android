package com.xiaoe.shop.wxb.business.mine.presenter;

import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;

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

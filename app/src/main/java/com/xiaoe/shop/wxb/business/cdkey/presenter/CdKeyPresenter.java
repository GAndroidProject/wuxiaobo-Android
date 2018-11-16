package com.xiaoe.shop.wxb.business.cdkey.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CdKeyRequest;
import com.xiaoe.network.requests.IRequest;

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
        cdKeyRequest.addRequestParam("code", code);
        NetworkEngine.getInstance().sendRequest(cdKeyRequest);
    }
}

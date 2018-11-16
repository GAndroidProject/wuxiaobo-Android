package com.xiaoe.shop.wxb.business.mine_learning.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.MineLearningRequest;

public class MineLearningPresenter implements IBizCallback {

    private INetworkResponse inr;

    public MineLearningPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 获取正在学习列表
    public void requestLearningData(int pageIndex, int pageSize) {
        MineLearningRequest mineLearningRequest = new MineLearningRequest(this);

        mineLearningRequest.addDataParam("page_size", pageSize);
        mineLearningRequest.addDataParam("page", pageIndex);

        NetworkEngine.getInstance().sendRequest(mineLearningRequest);
    }
}

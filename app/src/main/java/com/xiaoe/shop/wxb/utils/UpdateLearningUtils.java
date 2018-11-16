package com.xiaoe.shop.wxb.utils;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.UpdateMineLearningRequest;

public class UpdateLearningUtils implements IBizCallback {

    private INetworkResponse inr;

    public UpdateLearningUtils(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 更新学习记录
    public void updateLearningProgress(String resourceId, int resourceType, int progress) {
        UpdateMineLearningRequest updateMineLearningRequest = new UpdateMineLearningRequest(this);

        updateMineLearningRequest.addDataParam("resource_id", resourceId);
        updateMineLearningRequest.addDataParam("resource_type", resourceType);
        updateMineLearningRequest.addDataParam("learn_progress", progress);
        // 原始学习进度，app 没有进度，所以传 0，从头开始
        updateMineLearningRequest.addDataParam("org_learn_progress", "0");
        updateMineLearningRequest.addDataParam("spend_time", 0);

        NetworkEngine.getInstance().sendRequest(updateMineLearningRequest);
    }
}

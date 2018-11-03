package xiaoe.com.shop.utils;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.UpdateMineLearningRequest;

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
        UpdateMineLearningRequest updateMineLearningRequest = new UpdateMineLearningRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.learning.records.push/1.0.0", this);

        updateMineLearningRequest.addRequestParam("shop_id", Constants.getAppId());
        updateMineLearningRequest.addRequestParam("user_id", "u_5ad010f47073c_yeHaGL9bEG");
        updateMineLearningRequest.addDataParam("resource_id", resourceId);
        updateMineLearningRequest.addDataParam("resource_type", resourceType);
        updateMineLearningRequest.addDataParam("learn_progress", progress);
        // 原始学习进度，app 没有进度，所以传 0，从头开始
        updateMineLearningRequest.addDataParam("org_learn_progress", "0");

        NetworkEngine.getInstance().sendRequest(updateMineLearningRequest);
    }
}
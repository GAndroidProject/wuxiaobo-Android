package xiaoe.com.shop.business.mine_learning.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineLearningRequest;

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
        MineLearningRequest mineLearningRequest = new MineLearningRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.learning.records.get/1.0.0", this);

        mineLearningRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        mineLearningRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        mineLearningRequest.addDataParam("page_size", pageSize);
        mineLearningRequest.addDataParam("page", pageIndex);

        NetworkEngine.getInstance().sendRequest(mineLearningRequest);
    }
}

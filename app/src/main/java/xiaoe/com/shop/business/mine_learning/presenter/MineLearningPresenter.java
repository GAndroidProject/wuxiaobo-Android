package xiaoe.com.shop.business.mine_learning.presenter;

import xiaoe.com.common.entitys.TestInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineCollectionRequest;

public class MineLearningPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public MineLearningPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "test/test_app"; // 默认请求接口名字
    }

    public MineLearningPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        MineCollectionRequest mineCollectionRequest = new MineCollectionRequest(cmd, TestInfo.class, this);
        mineCollectionRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(mineCollectionRequest);
    }
}

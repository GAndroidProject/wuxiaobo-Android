package xiaoe.com.shop.business.search.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SearchRequest;

public class SearchPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public SearchPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = ""; // 默认接口
    }

    public SearchPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        SearchRequest searchRequest = new SearchRequest(cmd, null, this);
        searchRequest.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(searchRequest);
    }
}

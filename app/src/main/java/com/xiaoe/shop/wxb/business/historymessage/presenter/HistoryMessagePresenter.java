package com.xiaoe.shop.wxb.business.historymessage.presenter;

import com.xiaoe.common.entitys.HistoryMessageReq;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.GetHistoryMessageRequest;
import com.xiaoe.network.requests.IRequest;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：
 */
public class HistoryMessagePresenter implements IBizCallback {

    private INetworkResponse inr;

    public HistoryMessagePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestHistoryMessage(HistoryMessageReq historyMessageReq) {
        GetHistoryMessageRequest getHistoryMessageRequest = new GetHistoryMessageRequest(this);

        // 其他参数
        getHistoryMessageRequest.addRequestParam("buz_data", historyMessageReq.getBuz_data());

        getHistoryMessageRequest.sendRequest();
    }

}

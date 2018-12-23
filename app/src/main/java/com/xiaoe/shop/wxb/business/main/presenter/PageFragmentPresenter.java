package com.xiaoe.shop.wxb.business.main.presenter;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.PageFragmentRequest;

public class PageFragmentPresenter implements IBizCallback {

    private static final String TAG = "PageFragmentPresenter";

    private INetworkResponse inr;

    public PageFragmentPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 请求微页面数据
     * @param microPageId 微页面 id
     */
    public void requestMicroPageData(String microPageId) {

        PageFragmentRequest pageFragmentRequest = new PageFragmentRequest(this);
        pageFragmentRequest.addDataParam("micro_page_id", microPageId);
        pageFragmentRequest.addDataParam("client", "6");
        pageFragmentRequest.setNeedCache(true);
        pageFragmentRequest.setCacheKey(microPageId);

        pageFragmentRequest.sendRequest();
    }
}

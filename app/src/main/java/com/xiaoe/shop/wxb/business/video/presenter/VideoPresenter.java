package com.xiaoe.shop.wxb.business.video.presenter;

import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.ContentRequest;
import com.xiaoe.network.requests.CourseDetailRequest;
import com.xiaoe.network.requests.IRequest;

public class VideoPresenter implements IBizCallback {
    private static final String TAG = "VideoPresenter";
    private INetworkResponse iNetworkResponse;

    public VideoPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }

    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId){
        CourseDetailRequest courseDetailRequest = new CourseDetailRequest( this);
        courseDetailRequest.addDataParam("goods_id",resourceId);
        courseDetailRequest.addDataParam("goods_type",3);
        courseDetailRequest.addDataParam("agent_type",2);
        courseDetailRequest.setNeedCache(true);
        courseDetailRequest.setCacheKey(resourceId);
        courseDetailRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    @Deprecated
    public void requestContent(String resourceId){
        ContentRequest contentRequest = new ContentRequest( this);
        contentRequest.addRequestParam("resource_id",resourceId);
        contentRequest.addRequestParam("resource_type","3");
        contentRequest.sendRequest();
    }
}

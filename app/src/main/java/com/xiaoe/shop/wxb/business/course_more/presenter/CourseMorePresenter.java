package com.xiaoe.shop.wxb.business.course_more.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CommodityGroupRequest;
import com.xiaoe.network.requests.IRequest;

public class CourseMorePresenter implements IBizCallback {

    private INetworkResponse inr;

    public CourseMorePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    public CourseMorePresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 查看更多请求
    public void requestData(String groupId, int pageNum, int pageSize, String lastId) {
        CommodityGroupRequest commodityGroupRequest = new CommodityGroupRequest( null, this);
        commodityGroupRequest.addDataParam("id", groupId);
        commodityGroupRequest.addDataParam("page_num", pageNum);
        commodityGroupRequest.addDataParam("page_size", pageSize);
        commodityGroupRequest.addDataParam("client", "1");
        commodityGroupRequest.addDataParam("agent_type", 1);
        commodityGroupRequest.addDataParam("agent_version", "1");
        commodityGroupRequest.addDataParam("last_id", lastId);

        commodityGroupRequest.sendRequest();
    }
}

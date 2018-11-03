package xiaoe.com.shop.business.course_more.presenter;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CommodityGroupRequest;
import xiaoe.com.network.requests.IRequest;

public class CourseMorePresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public CourseMorePresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "xe.goods.more.get/1.0.0"; // 默认接口
    }

    public CourseMorePresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData(String groupId, int pageNum, int pageSize, String lastId) {
        CommodityGroupRequest commodityGroupRequest = new CommodityGroupRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + cmd, null, this);
        commodityGroupRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        commodityGroupRequest.addRequestParam("shop_id", Constants.getAppId());
        commodityGroupRequest.addRequestParam("app_version", "0.1");
        commodityGroupRequest.addRequestParam("client", "1");
        commodityGroupRequest.addRequestParam("is_manager", 0);
        commodityGroupRequest.addRequestParam("agent_type", 1);
        commodityGroupRequest.addRequestParam("agent_version", "1");
        commodityGroupRequest.addBUZDataParam("id", groupId);
        commodityGroupRequest.addBUZDataParam("page_num", pageNum);
        commodityGroupRequest.addBUZDataParam("page_size", pageSize);
        commodityGroupRequest.addBUZDataParam("last_id", lastId);
        NetworkEngine.getInstance().sendRequest(commodityGroupRequest);
    }
}

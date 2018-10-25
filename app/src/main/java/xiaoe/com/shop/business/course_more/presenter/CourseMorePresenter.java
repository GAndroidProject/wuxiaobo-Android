package xiaoe.com.shop.business.course_more.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseMoreRequest;
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

    public void requestData(String resourceId) {
        CourseMoreRequest courseMoreRequest = new CourseMoreRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + cmd, null, this);
        courseMoreRequest.addRequestParam("user_id", "u_5ad010f47073c_yeHaGL9bEG");
        courseMoreRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        courseMoreRequest.addRequestParam("app_version", "0.1");
        courseMoreRequest.addRequestParam("client", "1");
        courseMoreRequest.addRequestParam("is_manager", 0);
        courseMoreRequest.addRequestParam("agent_type", 1);
        courseMoreRequest.addRequestParam("agent_version", "1");
        courseMoreRequest.addBUZDataParam("id", resourceId);
        NetworkEngine.getInstance().sendRequest(courseMoreRequest);
    }
}

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
        this.cmd = ""; // 默认接口
    }

    public CourseMorePresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData() {
        CourseMoreRequest courseMoreRequest = new CourseMoreRequest(cmd, null, this);
        courseMoreRequest.addRequestParam("app_id", "123");
        NetworkEngine.getInstance().sendRequest(courseMoreRequest);
    }
}

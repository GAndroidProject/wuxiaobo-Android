package xiaoe.com.shop.business.course.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseRequest;
import xiaoe.com.network.requests.IRequest;

public class CoursePresenter implements IBizCallback {

    private INetworkResponse inr;

    public CoursePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onMainThreadResponse(iRequest, success, entity);
    }

    public void requestData() {
        CourseRequest cr = new CourseRequest("", null, this);
        cr.addRequestParam("app_id", "123456");
        NetworkEngine.getInstance().sendRequest(cr);
    }
}

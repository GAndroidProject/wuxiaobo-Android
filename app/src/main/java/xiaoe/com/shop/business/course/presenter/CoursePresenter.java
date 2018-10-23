package xiaoe.com.shop.business.course.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class CoursePresenter implements IBizCallback {

    private INetworkResponse inr;

    public CoursePresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (success && entity != null) {
                    inr.onMainThreadResponse(iRequest, success, entity);
                }
            }
        });
    }

    public void requestData() {
        CourseRequest cr = new CourseRequest(NetworkEngine.BASE_URL + "api/xe.shop.page.get/1.0.0", null, this);
        cr.addRequestParam("user_id", "u_5ad010f47073c_yeHaGL9bEG");
        cr.addRequestParam("shop_id", "apppcHqlTPT3482");
        cr.addRequestParam("micro_page_id", 0);
        NetworkEngine.getInstance().sendRequest(cr);
    }
}

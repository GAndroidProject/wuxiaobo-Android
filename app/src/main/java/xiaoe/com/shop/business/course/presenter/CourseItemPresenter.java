package xiaoe.com.shop.business.course.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseItemRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class CourseItemPresenter implements IBizCallback {

    private INetworkResponse inr;

    public CourseItemPresenter(INetworkResponse inr) {
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

    // 请求购买前的信息
    public void requestBeforeBuy () {
        CourseItemRequest courseItemRequest = new CourseItemRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"/xe.goods.info.get/1.0.0", this);
        courseItemRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        courseItemRequest.addRequestParam("resource_id","i_5bd0128d89b25_0j7fmF3M");
        courseItemRequest.addRequestParam("resource_type","1");
        courseItemRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        NetworkEngine.getInstance().sendRequest(courseItemRequest);
    }

    // 请求购买后的信息
    public void requestAfterBuy () {
        CourseItemRequest courseItemRequest = new CourseItemRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"/xe.resource.content.get", this);
        courseItemRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        courseItemRequest.addRequestParam("resource_id","a_5bc6b2a5d3ff5_cJc4CYI1");
        courseItemRequest.addRequestParam("resource_type","2");
        courseItemRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        NetworkEngine.getInstance().sendRequest(courseItemRequest);
    }
}

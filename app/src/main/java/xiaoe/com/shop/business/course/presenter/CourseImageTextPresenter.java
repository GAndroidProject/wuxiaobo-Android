package xiaoe.com.shop.business.course.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseITAfterBuyRequest;
import xiaoe.com.network.requests.CourseITBeforeBuyRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class CourseImageTextPresenter implements IBizCallback {

    private INetworkResponse inr;

    public CourseImageTextPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (success && entity != null) {
                    inr.onMainThreadResponse(iRequest, true, entity);
                } else {
                    inr.onMainThreadResponse(iRequest, false, entity);
                }
            }
        });
    }

    // 请求购买前的信息
    public void requestBeforeBuy (String resourceId, String resourceType) {
        CourseITBeforeBuyRequest courseITBeforeBuyRequest = new CourseITBeforeBuyRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.goods.info.get/1.0.0", this);
        courseITBeforeBuyRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        courseITBeforeBuyRequest.addRequestParam("resource_id", resourceId);
        courseITBeforeBuyRequest.addRequestParam("resource_type", resourceType);
        courseITBeforeBuyRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        NetworkEngine.getInstance().sendRequest(courseITBeforeBuyRequest);
    }

    // 请求购买后的信息
    public void requestAfterBuy (String resourceId, String resourceType) {
        CourseITAfterBuyRequest courseITAfterBuyRequest = new CourseITAfterBuyRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.resource.content.get/1.0.0", this);
        courseITAfterBuyRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        courseITAfterBuyRequest.addRequestParam("resource_id", resourceId);
        courseITAfterBuyRequest.addRequestParam("resource_type", resourceType);
        courseITAfterBuyRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        NetworkEngine.getInstance().sendRequest(courseITAfterBuyRequest);
    }
}

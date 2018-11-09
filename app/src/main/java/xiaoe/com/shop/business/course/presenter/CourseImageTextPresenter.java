package xiaoe.com.shop.business.course.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CourseITAfterBuyRequest;
import xiaoe.com.network.requests.CourseITBeforeBuyRequest;
import xiaoe.com.network.requests.CourseITDetailRequest;
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
        CourseITBeforeBuyRequest courseITBeforeBuyRequest = new CourseITBeforeBuyRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + "xe.goods.info.get/1.0.0", this);
        courseITBeforeBuyRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        courseITBeforeBuyRequest.addDataParam("resource_id", resourceId);
        courseITBeforeBuyRequest.addDataParam("resource_type", resourceType);
        courseITBeforeBuyRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        NetworkEngine.getInstance().sendRequest(courseITBeforeBuyRequest);
    }

    // 请求购买后的信息
    public void requestAfterBuy (String resourceId, String resourceType) {
        CourseITAfterBuyRequest courseITAfterBuyRequest = new CourseITAfterBuyRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + "xe.resource.content.get/1.0.0", this);
        courseITAfterBuyRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        courseITAfterBuyRequest.addDataParam("resource_id", resourceId);
        courseITAfterBuyRequest.addDataParam("resource_type", resourceType);
        courseITAfterBuyRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        NetworkEngine.getInstance().sendRequest(courseITAfterBuyRequest);
    }

    // 请求资源详情页信息（包含购买前和购买后）
    public void requestITDetail(String resourceId, int resourceType) {
        CourseITDetailRequest courseITDetailRequest = new CourseITDetailRequest(NetworkEngine.CLASS_DETAIL_BASE_URL + "xe.goods.detail.get/1.0.0", this);

        courseITDetailRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        courseITDetailRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        courseITDetailRequest.addDataParam("goods_id", resourceId);
        courseITDetailRequest.addDataParam("goods_type", resourceType);

        NetworkEngine.getInstance().sendRequest(courseITDetailRequest);
    }
}

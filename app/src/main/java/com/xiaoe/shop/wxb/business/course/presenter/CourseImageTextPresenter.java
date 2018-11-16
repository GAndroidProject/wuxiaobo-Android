package com.xiaoe.shop.wxb.business.course.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CourseITAfterBuyRequest;
import com.xiaoe.network.requests.CourseITBeforeBuyRequest;
import com.xiaoe.network.requests.CourseITDetailRequest;
import com.xiaoe.network.requests.IRequest;

public class CourseImageTextPresenter implements IBizCallback {

    private INetworkResponse inr;

    public CourseImageTextPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 请求购买前的信息
    public void requestBeforeBuy (String resourceId, String resourceType) {
        CourseITBeforeBuyRequest courseITBeforeBuyRequest = new CourseITBeforeBuyRequest(this);
        courseITBeforeBuyRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        courseITBeforeBuyRequest.addDataParam("resource_id", resourceId);
        courseITBeforeBuyRequest.addDataParam("resource_type", resourceType);
        courseITBeforeBuyRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        NetworkEngine.getInstance().sendRequest(courseITBeforeBuyRequest);
    }

    // 请求购买后的信息
    public void requestAfterBuy (String resourceId, String resourceType) {
        CourseITAfterBuyRequest courseITAfterBuyRequest = new CourseITAfterBuyRequest( this);
        courseITAfterBuyRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        courseITAfterBuyRequest.addDataParam("resource_id", resourceId);
        courseITAfterBuyRequest.addDataParam("resource_type", resourceType);
        courseITAfterBuyRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        NetworkEngine.getInstance().sendRequest(courseITAfterBuyRequest);
    }

    // 请求资源详情页信息（包含购买前和购买后）
    public void requestITDetail(String resourceId, int resourceType) {
        CourseITDetailRequest courseITDetailRequest = new CourseITDetailRequest( this);

        courseITDetailRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        courseITDetailRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        courseITDetailRequest.addDataParam("goods_id", resourceId);
        courseITDetailRequest.addDataParam("goods_type", resourceType);

        NetworkEngine.getInstance().sendRequest(courseITDetailRequest);
    }
}

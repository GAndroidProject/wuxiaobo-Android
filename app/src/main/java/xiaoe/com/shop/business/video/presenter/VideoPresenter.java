package xiaoe.com.shop.business.video.presenter;

import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.ContentRequest;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class VideoPresenter implements IBizCallback {
    private static final String TAG = "VideoPresenter";
    private INetworkResponse iNetworkResponse;

    public VideoPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                iNetworkResponse.onMainThreadResponse(iRequest, success, entity);
            }
        });
    }

    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId){
        DetailRequest couponRequest = new DetailRequest( this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id",resourceId);
        couponRequest.addRequestParam("resource_type","3");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    public void requestContent(String resourceId){
        ContentRequest couponRequest = new ContentRequest( this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id",resourceId);
        couponRequest.addRequestParam("resource_type","3");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }
}

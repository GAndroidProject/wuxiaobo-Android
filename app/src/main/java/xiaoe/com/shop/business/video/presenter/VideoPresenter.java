package xiaoe.com.shop.business.video.presenter;

import xiaoe.com.common.app.CommonUserInfo;
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
        DetailRequest detailRequest = new DetailRequest( this);
        detailRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        detailRequest.addDataParam("resource_id",resourceId);
        detailRequest.addDataParam("resource_type","3");
        detailRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        detailRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    public void requestContent(String resourceId){
        ContentRequest contentRequest = new ContentRequest( this);
        contentRequest.addRequestParam("shop_id",CommonUserInfo.getShopId());
        contentRequest.addRequestParam("resource_id",resourceId);
        contentRequest.addRequestParam("resource_type","3");
        contentRequest.addRequestParam("user_id",CommonUserInfo.getUserId());
        contentRequest.sendRequest();
    }
}

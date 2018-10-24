package xiaoe.com.shop.business.audio.presenter;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.AudioContentRequest;
import xiaoe.com.network.requests.AudioDetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class AudioPresenter implements IBizCallback {
    private static final String TAG = "AudioPresenter";
    private INetworkResponse iNetworkResponse;

    public AudioPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                if(success && entity != null){
                    iNetworkResponse.onMainThreadResponse(iRequest,success, entity);
                }
            }
        });

    }

    /**
     * 获取购买前商品详情
     */
    public void requestDetail(){
        AudioDetailRequest couponRequest = new AudioDetailRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.goods.info.get/1.0.0", this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id","a_5bc6b2a5d3ff5_cJc4CYI1");
        couponRequest.addRequestParam("resource_type","2");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    public void requestContent(){
        AudioContentRequest couponRequest = new AudioContentRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.resource.content.get/1.0.0", this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id","a_5bc6b2a5d3ff5_cJc4CYI1");
        couponRequest.addRequestParam("resource_type","2");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }
}

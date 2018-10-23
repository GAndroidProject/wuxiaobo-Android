package xiaoe.com.shop.business.audio.presenter;

import xiaoe.com.common.entitys.AudioGoodsDetail;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
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
        AudioDetailRequest couponRequest = new AudioDetailRequest(NetworkEngine.CLASS_DETAIL_BASE_URL+"xe.goods.info.get/1.0.0", AudioGoodsDetail.class, this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("goods_id","i_5bcde8b3943bd_PZIPQ3oF");
        couponRequest.addRequestParam("goods_type","1");
        couponRequest.addRequestParam("user_id","u_5859f1669c370_NI8t8WuW");
        couponRequest.sendRequest();
    }
}

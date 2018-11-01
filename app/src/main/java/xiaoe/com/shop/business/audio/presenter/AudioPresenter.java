package xiaoe.com.shop.business.audio.presenter;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.ContentRequest;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;
import xiaoe.com.shop.events.AudioPlayEvent;

public class AudioPresenter implements IBizCallback {
    private static final String TAG = "AudioPresenter";
    private INetworkResponse iNetworkResponse;

    public AudioPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(iRequest instanceof DetailRequest){
            setAudioDetail(success, (JSONObject) entity, (String) iRequest.getDataParams().get("resource_id"));
        }else if(iRequest instanceof ContentRequest){
            setAudioContent(success, (JSONObject)entity);
        }else{
            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if(success && entity != null){
                        iNetworkResponse.onMainThreadResponse(iRequest,success, entity);
                    }
                }
            });
        }

    }

    private void setAudioContent(boolean success, JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            playEntity = new AudioPlayEntity();
        }
        if(!success || jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            playEntity.setCode(1);
            playAudio(playEntity.isPlay());
            return;
        }
        playEntity.setPlayUrl(data.getString("audio_url"));
        playEntity.setContent(data.getString("content"));
        playEntity.setCode(0);
//        AudioMediaPlayer.setAudio(playEntity, false);
        playAudio(playEntity.isPlay());
    }

    private void setAudioDetail(boolean success, JSONObject jsonObject, String resourceId) {
        JSONObject data = jsonObject.getJSONObject("data");
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            playEntity = new AudioPlayEntity();
            playEntity.setResourceId(resourceId);
            playEntity.setAppId("apppcHqlTPT3482");
            playEntity.setIndex(0);
            playEntity.setPlay(false);
        }
        if(!resourceId.equals(playEntity.getResourceId())){
            //当前播放的音频与请求到的音频数据不是同一资源，则放弃结果
            return;
        }
        playEntity.setCurrentPlayState(1);
        if(!success || jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || data == null ){
            playEntity.setCode(1);
            return;
        }
        JSONObject resourceInfo = data.getJSONObject("resource_info");
        playEntity.setTitle(resourceInfo.getString("title"));
        playEntity.setHasFavorite(resourceInfo.getIntValue("has_favorite"));
        playEntity.setPlayCount(resourceInfo.getIntValue("audio_play_count"));
        playEntity.setHasBuy(resourceInfo.getIntValue("has_buy"));
        playEntity.setResourceId(resourceId);
        playEntity.setPrice(resourceInfo.getIntValue("price"));
        if(resourceInfo.getIntValue("has_buy") == 0){
            playEntity.setCode(0);
            playEntity.setContent(resourceInfo.getString("content"));
            playAudio(false);
        }else{
            requestContent(resourceId);
        }
    }

    private void playAudio(final boolean play){
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                AudioPlayEvent event = new AudioPlayEvent();
                event.setState(AudioPlayEvent.REFRESH_PAGER);
                EventBus.getDefault().post(event);
//                if(play){
//                    AudioMediaPlayer.start();
//                }
            }
        });
    }

    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId){
        DetailRequest couponRequest = new DetailRequest( this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addDataParam("resource_id",resourceId);
        couponRequest.addDataParam("resource_type","2");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    private void requestContent(String resourceId){
        ContentRequest couponRequest = new ContentRequest( this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id",resourceId);
        couponRequest.addRequestParam("resource_type","2");
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }
}

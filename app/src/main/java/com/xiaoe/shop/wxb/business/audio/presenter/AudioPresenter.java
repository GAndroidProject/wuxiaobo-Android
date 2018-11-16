package com.xiaoe.shop.wxb.business.audio.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.ContentRequest;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;

public class AudioPresenter implements IBizCallback {
    private static final String TAG = "AudioPresenter";
    private INetworkResponse iNetworkResponse;

    public AudioPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if (entity == null)   return;//修复报空指针
        if(iRequest instanceof DetailRequest){
            setAudioDetail(success, (JSONObject) entity, (String) iRequest.getDataParams().get("goods_id"));
        }else if(iRequest instanceof ContentRequest){
            setAudioContent(success, (JSONObject)entity);
        }else{
            iNetworkResponse.onResponse(iRequest, success, entity);
        }

    }

    private void setAudioContent(boolean success, JSONObject jsonObject) {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            playEntity = new AudioPlayEntity();
        }
        if(!success || jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED ){
            playEntity.setCode(1);
            playAudio(playEntity.isPlay());
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");

        playEntity.setPlayUrl(data.getString("audio_url"));
        playEntity.setContent(data.getString("content"));
        playEntity.setCode(0);
//        AudioMediaPlayer.setAudio(playEntity, false);
        playAudio(playEntity.isPlay());
    }

    private void setAudioDetail(boolean success, JSONObject jsonObject, String resourceId) {

        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            playEntity = new AudioPlayEntity();
            playEntity.setResourceId(resourceId);
            playEntity.setAppId(CommonUserInfo.getShopId());
            playEntity.setIndex(0);
            playEntity.setPlay(false);
        }
        if(!success || jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED ){
            playEntity.setCode(1);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if(!resourceId.equals(playEntity.getResourceId())){
            //当前播放的音频与请求到的音频数据不是同一资源，则放弃结果
            return;
        }
        boolean available = data.getBoolean("available");
        JSONObject resourceInfo = null;
        if(available){
            resourceInfo = data;
            JSONObject shareInfo = resourceInfo.getJSONObject("share_info");
            if(shareInfo != null && shareInfo.getJSONObject("wx") != null){
                playEntity.setShareUrl(shareInfo.getJSONObject("wx").getString("share_url"));
            }
        }else{
            resourceInfo = data.getJSONObject("resource_info");
        }
        playEntity.setCurrentPlayState(1);
        playEntity.setTitle(resourceInfo.getString("title"));
        playEntity.setHasFavorite(resourceInfo.getIntValue("has_favorite"));
        playEntity.setPlayCount(resourceInfo.getIntValue("audio_play_count"));
        playEntity.setHasBuy(available ? 1 : 0);
        playEntity.setResourceId(resourceInfo.getString("resource_id"));
        playEntity.setPrice(resourceInfo.getIntValue("price"));
        playEntity.setImgUrl(resourceInfo.getString("img_url"));
        playEntity.setImgUrlCompressed(resourceInfo.getString("img_url_compressed"));

        //如果存在本地音频则播放本地是否
        DownloadResourceTableInfo download = DownloadManager.getInstance().getDownloadFinish(CommonUserInfo.getShopId(), resourceId);
        String localAudioPath = "";
        if(download != null){
            File file = new File(download.getLocalFilePath());
            if(file.exists()){
                localAudioPath = download.getLocalFilePath();
            }
        }
        if(available){
            if(TextUtils.isEmpty(localAudioPath)){
                playEntity.setPlayUrl(resourceInfo.getString("audio_url"));
            }else{
                playEntity.setPlayUrl(localAudioPath);
            }
            playEntity.setContent(resourceInfo.getString("content"));
            playEntity.setCode(0);
            playEntity.setAudioResourceId(resourceInfo.getString("resource_id"));
            playAudio(playEntity.isPlay());
        }else{
            playEntity.setAudioResourceId(resourceInfo.getString("resource_id"));
            playEntity.setCode(0);
            playEntity.setContent(resourceInfo.getString("content"));
            playAudio(false);
        }
    }

    private void playAudio(final boolean play){
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                AudioPlayEvent event = new AudioPlayEvent();
                event.setState(AudioPlayEvent.REFRESH_PAGER);
                EventBus.getDefault().post(event);
            }
        });
    }

    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId){
        DetailRequest detailRequest = new DetailRequest( this);
        detailRequest.addDataParam("goods_id",resourceId);
        detailRequest.addDataParam("goods_type",2);
        detailRequest.sendRequest();
    }

    /**
     * 获取购买后的资源内容
     */
    private void requestContent(String resourceId){
        ContentRequest contentRequest = new ContentRequest( this);
        contentRequest.addRequestParam("resource_id",resourceId);
        contentRequest.addRequestParam("resource_type",2);
        contentRequest.sendRequest();
    }
}

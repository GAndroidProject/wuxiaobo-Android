package com.xiaoe.shop.wxb.business.audio.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.ContentRequest;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

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
            int hasFavorite = ((JSONObject) resourceInfo.get("favorites_info")).getInteger("is_favorite");
            playEntity.setHasFavorite(hasFavorite);
        }else{
            resourceInfo = data.getJSONObject("resource_info");
            int hasFavorite = ((JSONObject) data.get("favorites_info")).getInteger("is_favorite");
            playEntity.setHasFavorite(hasFavorite);
        }
        playEntity.setCurrentPlayState(1);
        playEntity.setTitle(resourceInfo.getString("title"));

        playEntity.setPlayCount(resourceInfo.getIntValue("audio_play_count"));
        playEntity.setHasBuy(available ? 1 : 0);
        playEntity.setResourceId(resourceInfo.getString("resource_id"));
        playEntity.setPrice(resourceInfo.getIntValue("price"));
        playEntity.setImgUrl(resourceInfo.getString("img_url"));
        playEntity.setImgUrlCompressed(resourceInfo.getString("img_url_compressed"));
        playEntity.setFlowId(resourceId);
        // isFree -- 1 免费，0 付费
        int isFree = resourceInfo.getInteger("is_free") == null ? 0 : resourceInfo.getInteger("is_free");
        playEntity.setFree(isFree != 0);

        //如果存在本地音频则播放本地是否
        DownloadResourceTableInfo download = DownloadManager.getInstance().getDownloadFinish(Constants.getAppId(), resourceId);
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
            playAudio(playEntity.isPlay());
        }else{
            if(resourceInfo.getIntValue("payment_type") == 3){
                //1-免费,2-单卖，3-非单卖
                //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
                //1-免费,2-单卖，3-非单卖
                //非单卖需要跳转到所属专栏，如果所属专栏多个，只跳转第一个
                JSONArray productList = data.getJSONObject("product_info").getJSONArray("product_list");
                JSONObject product = productList.getJSONObject(0);
                int productType = product.getIntValue("product_type");
                String productId = product.getString("id");
                String productImgUrl = product.getString("img_url");
                //1-专栏, 2-会员, 3-大专栏
               playEntity.setSingleBuy(false);
               playEntity.setProductId(productId);
               playEntity.setProductImgUrl(productImgUrl);
               if(productType == 3){
                   playEntity.setProductType(8);
               }else if(productType == 2){
                   playEntity.setProductType(5);
               }else{
                   playEntity.setProductType(6);
               }
            }

            playEntity.setCode(0);
            playEntity.setContent(resourceInfo.getString("preview_content"));
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
     * 获取商品详情
     */
    public void requestDetail(String resourceId){
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
        String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='"+resourceId+"'";
        List<CacheData> cacheDataList = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sql, null );
        if(cacheDataList != null && cacheDataList.size() > 0){
            JSONObject data = JSONObject.parseObject(cacheDataList.get(0).getContent());
            setAudioDetail(true, data, resourceId);
        }
        DetailRequest detailRequest = new DetailRequest( this);
        detailRequest.addDataParam("goods_id",resourceId);
        detailRequest.addDataParam("goods_type",2);
        detailRequest.setNeedCache(true);
        detailRequest.setCacheKey(resourceId);
        detailRequest.sendRequest();
    }

}

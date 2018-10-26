package xiaoe.com.shop.business.column.presenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.ColumnListRequst;
import xiaoe.com.network.requests.DetailRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class ColumnPresenter implements IBizCallback {
    private static final String TAG = "ColumnPresenter";

    private INetworkResponse iNetworkResponse;

    public ColumnPresenter(INetworkResponse iNetworkResponse) {
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
    public void requestDetail(String resourceId, String resourceType){
        DetailRequest couponRequest = new DetailRequest( this);
        couponRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        couponRequest.addRequestParam("resource_id",resourceId);
        couponRequest.addRequestParam("resource_type",resourceType);
        couponRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        couponRequest.sendRequest();
    }

    public void requestColumnList(String resourceId, String resourceType){
        ColumnListRequst columnListRequst = new ColumnListRequst(this);
        columnListRequst.addRequestParam("shop_id","apppcHqlTPT3482");
        columnListRequst.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        columnListRequst.addDataParam("goods_id",resourceId);
        columnListRequst.addDataParam("resource_type",resourceType);
        columnListRequst.sendRequest();
    }

    /**
     * 格式化一级目录数据
     * @param jsonArray
     */
    public List<ColumnDirectoryEntity> formatColumnEntity(JSONArray jsonArray){
        List<ColumnDirectoryEntity> directoryEntityList = new ArrayList<ColumnDirectoryEntity>();
        for (Object object : jsonArray) {
            ColumnDirectoryEntity directoryEntity = new ColumnDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            directoryEntity.setApp_id(jsonObject.getString("app_id"));
            directoryEntity.setTitle(jsonObject.getString("title"));
            directoryEntity.setResource_id(jsonObject.getString("resource_id"));
            directoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            List<ColumnSecondDirectoryEntity> childList = formatSingleResouceEntity(jsonObject.getJSONArray("resource_list"));
            directoryEntity.setResource_list(childList);
            directoryEntityList.add(directoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 格式化二级目录数据
     * @param jsonArray
     * @return
     */
    public List<ColumnSecondDirectoryEntity> formatSingleResouceEntity(JSONArray jsonArray){
        List<ColumnSecondDirectoryEntity> directoryEntityList = new ArrayList<ColumnSecondDirectoryEntity>();
        for (Object object : jsonArray) {
            ColumnSecondDirectoryEntity secondDirectoryEntity = new ColumnSecondDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            secondDirectoryEntity.setApp_id(jsonObject.getString("app_id"));
            secondDirectoryEntity.setResource_id(jsonObject.getString("resource_id"));
            secondDirectoryEntity.setTitle(jsonObject.getString("title"));
            secondDirectoryEntity.setImg_url(jsonObject.getString("img_url"));
            secondDirectoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            secondDirectoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            secondDirectoryEntity.setAudio_length(jsonObject.getIntValue("audio_length"));
            secondDirectoryEntity.setVideo_length(jsonObject.getIntValue("video_length"));
            secondDirectoryEntity.setAudio_url(jsonObject.getString("audio_url"));
            directoryEntityList.add(secondDirectoryEntity);
        }
        return directoryEntityList;
    }
}

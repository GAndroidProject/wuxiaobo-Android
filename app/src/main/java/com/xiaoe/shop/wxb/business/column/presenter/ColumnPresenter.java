package com.xiaoe.shop.wxb.business.column.presenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.DetailRequest;
import com.xiaoe.network.requests.IRequest;

import java.util.ArrayList;
import java.util.List;

public class ColumnPresenter implements IBizCallback {
    private static final String TAG = "ColumnPresenter";

    private INetworkResponse iNetworkResponse;

    public ColumnPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }
    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId, String resourceType){
        DetailRequest detailRequest = new DetailRequest( this);
        detailRequest.addDataParam("goods_id",resourceId);
        detailRequest.addDataParam("goods_type",Integer.parseInt(resourceType));
        detailRequest.setNeedCache(true);
        detailRequest.setCacheKey(resourceId);
        detailRequest.sendRequest();
    }

    public void requestColumnList(String resourceId, String resourceType, int page, int pageSize){
        ColumnListRequst columnListRequst = new ColumnListRequst(this);
        columnListRequst.addDataParam("goods_id",resourceId);
        columnListRequst.addDataParam("resource_type",resourceType);
        columnListRequst.addDataParam("page", ""+page);
        columnListRequst.addDataParam("page_size", ""+pageSize);
        columnListRequst.setNeedCache(true);
        columnListRequst.setCacheKey(resourceId+"_list");
        columnListRequst.sendRequest();
    }

    /**
     * 格式化一级目录数据
     * @param jsonArray
     */
    public List<ColumnDirectoryEntity> formatColumnEntity(JSONArray jsonArray, String bigColumnId){
        List<ColumnDirectoryEntity> directoryEntityList = new ArrayList<ColumnDirectoryEntity>();
        for (Object object : jsonArray) {
            ColumnDirectoryEntity directoryEntity = new ColumnDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            directoryEntity.setApp_id(jsonObject.getString("app_id"));
            directoryEntity.setTitle(jsonObject.getString("title"));
            String columnId = jsonObject.getString("resource_id");
            directoryEntity.setImg_url(jsonObject.getString("img_url"));
            directoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            directoryEntity.setResource_id(columnId);
            directoryEntity.setBigColumnId(bigColumnId);
            directoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            List<ColumnSecondDirectoryEntity> childList = formatSingleResourceEntity(jsonObject.getJSONArray("resource_list"), directoryEntity.getTitle(), columnId , bigColumnId);
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
    public List<ColumnSecondDirectoryEntity> formatSingleResourceEntity(JSONArray jsonArray, String columnTitle, String columnId, String bigColumnId){
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
            secondDirectoryEntity.setColumnTitle(columnTitle);
            secondDirectoryEntity.setColumnId(columnId);
            secondDirectoryEntity.setBigColumnId(bigColumnId);
            secondDirectoryEntity.setIsTry(jsonObject.getIntValue("is_try"));

            boolean isDownload = DownloadManager.getInstance().isDownload(secondDirectoryEntity.getApp_id(),secondDirectoryEntity.getResource_id());
            secondDirectoryEntity.setEnable(!isDownload);

            directoryEntityList.add(secondDirectoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 根据每页大小请求专栏列表
     * @param resourceId    资源 id
     * @param resourceType  资源类型
     * @param pageSize      每页数量
     */
    public void requestColumnListByNum(String resourceId, String resourceType, int pageSize) {
        ColumnListRequst columnListRequst = new ColumnListRequst(this);
        columnListRequst.addDataParam("goods_id",resourceId);
        columnListRequst.addDataParam("resource_type",resourceType);
        columnListRequst.addDataParam("page", "1");
        columnListRequst.addDataParam("page_size", pageSize + "");
        columnListRequst.sendRequest();
    }
}

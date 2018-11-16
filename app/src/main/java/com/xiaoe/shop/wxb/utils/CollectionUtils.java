package com.xiaoe.shop.wxb.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.AddCollectionRequest;
import com.xiaoe.network.requests.CheckCollectionRequest;
import com.xiaoe.network.requests.CollectionListRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.RemoveCollectioinByObjRequest;
import com.xiaoe.network.requests.RemoveCollectionListRequest;

/**
 * 收藏操作工具类
 */
public class CollectionUtils implements IBizCallback {

    private INetworkResponse inr;

    public CollectionUtils(INetworkResponse inr) {
        this.inr = inr;
    }

    public CollectionUtils() {
        this.inr = new INetworkResponse() {
            @Override
            public void onResponse(IRequest iRequest, boolean success, Object entity) {

            }

            @Override
            public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

            }
        };
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 请求检查是否收藏
     * @param resourceId   检查是否收藏的资源 id
     * @param resourceType 检查是否收藏的资源类型
     */
    public void requestCheckCollection(String resourceId, String resourceType) {
        CheckCollectionRequest checkCollectionRequest = new CheckCollectionRequest(this);
        checkCollectionRequest.addDataParam("content_id", resourceId);
        checkCollectionRequest.addDataParam("content_type", Integer.parseInt(resourceType));
        NetworkEngine.getInstance().sendRequest(checkCollectionRequest);
    }

    /**
     * 请求添加收藏
     * @param resourceId        添加收藏的资源 id
     * @param resourceType      添加收藏的资源类型
     * @param collectionContent 添加收藏的内容
     */
    public void requestAddCollection(String resourceId, String resourceType, JSONObject collectionContent) {
        AddCollectionRequest addCollectionRequest = new AddCollectionRequest(this);
        addCollectionRequest.addDataParam("content_id", resourceId);
        addCollectionRequest.addDataParam("content_type", Integer.parseInt(resourceType));
        addCollectionRequest.addDataParam("content", collectionContent);
        addCollectionRequest.addDataParam("type", 1);
        NetworkEngine.getInstance().sendRequest(addCollectionRequest);
    }

    /**
     *
     * @param resourceId
     * @param resourceType
     */
    public void requestRemoveCollection(String resourceId, String resourceType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content_id", resourceId);
        jsonObject.put("content_type", Integer.parseInt(resourceType));
        jsonObject.put("type", 1);
        List<JSONObject> delList = new ArrayList<JSONObject>();
        delList.add(jsonObject);
        RemoveCollectionListRequest removeCollectionRequest = new RemoveCollectionListRequest( this);
        removeCollectionRequest.addDataParam("del_list", delList);
        NetworkEngine.getInstance().sendRequest(removeCollectionRequest);
    }


    /**
     * 请求收藏列表
     * @param pageIndex 收藏列表页面下标
     * @param pageSize  收藏列表每页大小
     */
    public void requestCollectionList(int pageIndex, int pageSize) {
        CollectionListRequest collectionListRequest = new CollectionListRequest(this);
        collectionListRequest.addDataParam("page", pageIndex);
        collectionListRequest.addDataParam("page_size", pageSize);
        NetworkEngine.getInstance().sendRequest(collectionListRequest);
    }

    /**
     * 删除指定收藏列表
     * @param delList 删除收藏的列表对象，包括 content_id（资源 id）、content_type（资源类型）、type（收藏商品，默认 1）
     */
    public void requestRemoveAllCollection(JSONObject delList) {
        RemoveCollectioinByObjRequest removeCollectioinByObjRequest = new RemoveCollectioinByObjRequest(this);
        removeCollectioinByObjRequest.addDataParam("del_list", delList);
        NetworkEngine.getInstance().sendRequest(removeCollectioinByObjRequest);
    }
}

package xiaoe.com.shop.utils;

import com.alibaba.fastjson.JSONObject;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.AddCollectionRequest;
import xiaoe.com.network.requests.CheckCollectionRequest;
import xiaoe.com.network.requests.CollectionListRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.RemoveCollectioinByObjRequest;
import xiaoe.com.network.requests.RemoveCollectionRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

/**
 * 收藏操作工具类
 */
public class CollectionUtils implements IBizCallback {

    private INetworkResponse inr;

    public CollectionUtils(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (success && entity != null) {
                    inr.onMainThreadResponse(iRequest, true, entity);
                } else {
                    inr.onMainThreadResponse(iRequest, false, entity);
                }
            }
        });
    }

    /**
     * 请求检查是否收藏
     * @param resourceId   检查是否收藏的资源 id
     * @param resourceType 检查是否收藏的资源类型
     */
    public void requestCheckCollection(String resourceId, String resourceType) {
        CheckCollectionRequest checkCollectionRequest = new CheckCollectionRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.favorites.check/1.0.0", this);
        checkCollectionRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        checkCollectionRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
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
        AddCollectionRequest addCollectionRequest = new AddCollectionRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.favorites.add/1.0.0", this);
        addCollectionRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        addCollectionRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        addCollectionRequest.addDataParam("content_id", resourceId);
        addCollectionRequest.addDataParam("content_type", Integer.parseInt(resourceType));
        addCollectionRequest.addDataParam("content", collectionContent);
        addCollectionRequest.addDataParam("type", 1);
        NetworkEngine.getInstance().sendRequest(addCollectionRequest);
    }

    /**
     * 请求删除收藏
     * @param resourceId   删除收藏的资源 id
     * @param resourceType 删除受从昂的资源类型
     */
    public void requestRemoveCollection(String resourceId, String resourceType) {
        RemoveCollectionRequest removeCollectionRequest = new RemoveCollectionRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.favorites.del/1.0.0", this);
        removeCollectionRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        removeCollectionRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        removeCollectionRequest.addDataParam("content_id", resourceId);
        removeCollectionRequest.addDataParam("content_type", Integer.parseInt(resourceType));
        removeCollectionRequest.addDataParam("type", 1);
        NetworkEngine.getInstance().sendRequest(removeCollectionRequest);
    }

    /**
     * 请求收藏列表
     * @param pageIndex 收藏列表页面下标
     * @param pageSize  收藏列表每页大小
     */
    public void requestCollectionList(int pageIndex, int pageSize) {
        CollectionListRequest collectionListRequest = new CollectionListRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.favorites.get/1.0.0", this);
        collectionListRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        collectionListRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        collectionListRequest.addDataParam("page", pageIndex);
        collectionListRequest.addDataParam("page_size", pageSize);
        NetworkEngine.getInstance().sendRequest(collectionListRequest);
    }

    /**
     * 删除指定收藏列表
     * @param delList 删除收藏的列表对象，包括 content_id（资源 id）、content_type（资源类型）、type（收藏商品，默认 1）
     */
    public void requestRemoveAllCollection(JSONObject delList) {
        RemoveCollectioinByObjRequest removeCollectioinByObjRequest = new RemoveCollectioinByObjRequest(NetworkEngine.COLLECTION_BASE_URL + "xe.user.favorites.multi.del/1.0.0", this);
        removeCollectioinByObjRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        removeCollectioinByObjRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        removeCollectioinByObjRequest.addDataParam("del_list", delList);
        NetworkEngine.getInstance().sendRequest(removeCollectioinByObjRequest);
    }
}

package com.xiaoe.shop.wxb.common.releaseversion.presenter;

import android.text.TextUtils;

import com.xiaoe.common.entitys.ReleaseVersionReq;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.ReleaseVersionRequest;

import java.util.HashMap;

public class ReleaseVersionPresenter implements IBizCallback {

    private INetworkResponse iNetworkResponse;

    public ReleaseVersionPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }

    public void requestReleaseVersion(String url, ReleaseVersionReq releaseVersionReq) {
        ReleaseVersionRequest releaseVersionRequest = new ReleaseVersionRequest(url, this);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("app_id", releaseVersionReq.getApp_id());
        hashMap.put("shop_id", releaseVersionReq.getShop_id());
        hashMap.put("version", releaseVersionReq.getVersion());
        hashMap.put("type", releaseVersionReq.getType());
        hashMap.put("download_url", releaseVersionReq.getDownload_url());
        hashMap.put("update_mode", releaseVersionReq.getUpdate_mode());
        if (!TextUtils.isEmpty(releaseVersionReq.getAudit_version())) {
            hashMap.put("audit_version", releaseVersionReq.getAudit_version());
        }
        hashMap.put("msg", releaseVersionReq.getMsg());

        releaseVersionRequest.addRequestParam(hashMap);

        releaseVersionRequest.sendRequestSimple();
    }
}

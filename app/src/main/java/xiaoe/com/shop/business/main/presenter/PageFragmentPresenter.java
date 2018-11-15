package xiaoe.com.shop.business.main.presenter;

import android.util.Log;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PageFragmentRequest;

public class PageFragmentPresenter implements IBizCallback {

    private static final String TAG = "PageFragmentPresenter";

    private INetworkResponse inr;

    public PageFragmentPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 请求微页面数据
     * @param microPageId 微页面 id
     */
    public void requestMicroPageData(String microPageId) {
        // appiOW1KfWe9943 u_5bd995b7db6ac_65ACBvuFTU -- 暂时测信息流
        // apppcHqlTPT3482 u_5ad010f47073c_yeHaGL9bEG -- 主干数据
        if ("".equals(CommonUserInfo.getUserId()) || "".equals(CommonUserInfo.getShopId())) {
            // 登录用户信息为空
            Log.d(TAG, "requestMicroPageData: 游客登录吧....");
            return;
        }
//        PageFragmentRequest pageFragmentRequest = new PageFragmentRequest(NetworkEngine.BASE_URL + "api/xe.shop.page.get/1.0.0", this);
        PageFragmentRequest pageFragmentRequest = new PageFragmentRequest(NetworkEngine.API_THIRD_BASE_URL + "xe.shop.page.get/1.0.0", this);
        pageFragmentRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        pageFragmentRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        pageFragmentRequest.addDataParam("micro_page_id", microPageId);
        // 支付的信息
//        int id = 0;
//        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
//            id = 0;
//        } else if (microPageId.equals(MainActivity.MICRO_PAGE_COURSE)) {
//            id = 6232;
//        }
//        pageFragmentRequest.addRequestParam("micro_page_id", id);
        NetworkEngine.getInstance().sendRequest(pageFragmentRequest);
    }
}

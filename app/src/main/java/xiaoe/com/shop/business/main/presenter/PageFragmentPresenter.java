package xiaoe.com.shop.business.main.presenter;

import xiaoe.com.common.app.Constants;
import xiaoe.com.common.entitys.TestComponent;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PageFragmentRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;
import xiaoe.com.shop.business.main.ui.MainActivity;

public class PageFragmentPresenter implements IBizCallback {

    private INetworkResponse inr;

    public PageFragmentPresenter(INetworkResponse inr) {
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
     * 请求微页面数据
     * @param microPageId 微页面 id
     */
    public void requestMicroPageData(String microPageId) {
        // appiOW1KfWe9943 u_5bd995b7db6ac_65ACBvuFTU -- 暂时测信息流
        // apppcHqlTPT3482 u_5ad010f47073c_yeHaGL9bEG -- 主干数据
        PageFragmentRequest pageFragmentRequest = new PageFragmentRequest(NetworkEngine.BASE_URL + "api/xe.shop.page.get/1.0.0", TestComponent.class, this);
        pageFragmentRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        pageFragmentRequest.addRequestParam("shop_id", Constants.getAppId());
//        pageFragmentRequest.addRequestParam("micro_page_id", microPageId);
        // 支付的信息
        int id = 0;
        if (microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) {
            id = 0;
        } else if (microPageId.equals(MainActivity.MICRO_PAGE_COURSE)) {
            id = 6232;
        }
        pageFragmentRequest.addRequestParam("micro_page_id", id);
        NetworkEngine.getInstance().sendRequest(pageFragmentRequest);
    }
}

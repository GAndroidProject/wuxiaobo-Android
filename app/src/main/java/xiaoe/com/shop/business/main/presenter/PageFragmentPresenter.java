package xiaoe.com.shop.business.main.presenter;

import xiaoe.com.common.entitys.TestComponent;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.ColumnListRequst;
import xiaoe.com.network.requests.PageFragmentRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

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
    public void requestMicroPageData(int microPageId) {
        PageFragmentRequest pageFragmentRequest = new PageFragmentRequest(NetworkEngine.BASE_URL + "api/xe.shop.page.get/1.0.0", TestComponent.class, this);
        pageFragmentRequest.addRequestParam("user_id", "u_5ad010f47073c_yeHaGL9bEG");
        pageFragmentRequest.addRequestParam("shop_id", "apppcHqlTPT3482");
        pageFragmentRequest.addRequestParam("micro_page_id", microPageId);
        NetworkEngine.getInstance().sendRequest(pageFragmentRequest);
    }
}

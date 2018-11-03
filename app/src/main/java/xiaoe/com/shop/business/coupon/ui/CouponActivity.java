package xiaoe.com.shop.business.coupon.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.CouponInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.MineCouponRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.coupon.presenter.CouponPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.interfaces.OnSelectCouponListener;
import xiaoe.com.shop.widget.StatusPagerView;

public class CouponActivity extends XiaoeActivity implements View.OnClickListener, OnSelectCouponListener {

    private static final String TAG = "CouponActivity";

    protected static final String EMPTY_CONTENT = "empty_content";
    private final int LOADING = 10001;
    private final int FINISH = 10002;
    private final int FAIL = 10003;
    private final int EMPTY = 10004;

    private ImageView couponBack;

    private CouponPresenter mCouponPresenter;
    private StatusPagerView statusPagerView;
    private CouponFragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);


        mCouponPresenter = new CouponPresenter(this);
        initView();
        initListener();
        setPagerState(LOADING);
        mCouponPresenter.requestMineCoupon("all");
    }

    private void initView() {
        couponBack = (ImageView) findViewById(R.id.coupon_back);

        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);

        currentFragment = CouponFragment.newInstance(R.layout.fragment_coupone);
        currentFragment.setMarginTop(Dp2Px2SpUtil.dp2px(this, 20));
        getSupportFragmentManager().beginTransaction().add(R.id.coupon_content_wrap, currentFragment, EMPTY_CONTENT).commit();
    }

    private void initListener() {
        couponBack.setOnClickListener(this);
        currentFragment.setOnSelectCouponListener(this);
    }

    private void setPagerState(int state){
        if(state == FINISH){
            statusPagerView.setVisibility(View.GONE);
            return;
        }
        statusPagerView.setVisibility(View.VISIBLE);
        if(state == LOADING){
            statusPagerView.setLoadingState(View.VISIBLE);
            statusPagerView.setHintStateVisibility(View.GONE);
        }else if (state == FAIL){
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
            statusPagerView.setStateImage(R.mipmap.network_none);
            statusPagerView.setStateText(getResources().getString(R.string.request_fail));
        }else if (state == EMPTY){
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.GONE);
            statusPagerView.setStateImage(R.mipmap.network_none);
            statusPagerView.setStateText(getResources().getString(R.string.coupon_empty_desc));
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        if(success){
            if(iRequest instanceof MineCouponRequest){
                JSONObject jsonObject = (JSONObject) entity;
                mineCouponRequest(jsonObject);
            }
        }else{
            setPagerState(FAIL);
        }
    }

    private void mineCouponRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            setPagerState(FAIL);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");

        //可用优惠券
        List<CouponInfo> validCoupon = data.getJSONArray("valid").toJavaList(CouponInfo.class);
        //可用的优惠券
        List<CouponInfo> invalidCoupon = data.getJSONArray("invalid").toJavaList(CouponInfo.class);
        if(validCoupon.size() <= 0 && invalidCoupon.size() <= 0){
            setPagerState(EMPTY);
            return;
        }
        setPagerState(FINISH);
        List<CouponInfo> couponList = new ArrayList<CouponInfo>();
        for (CouponInfo couponInfo : validCoupon) {
            couponInfo.setValid(true);
            couponList.add(couponInfo);
        }
        for (CouponInfo couponInfo : invalidCoupon) {
            couponInfo.setValid(false);
            couponList.add(couponInfo);
        }


        currentFragment.addData(couponList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coupon_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelect(CouponInfo couponInfo, int position) {
        if(couponInfo.getType() == 0){
            JumpDetail.jumpCouponCanRerource(this, couponInfo);
        }
    }
}

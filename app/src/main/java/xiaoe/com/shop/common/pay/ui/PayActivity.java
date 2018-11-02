package xiaoe.com.shop.common.pay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.CouponInfo;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.PayOrderRequest;
import xiaoe.com.network.requests.ResourceUseCouponRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.coupon.presenter.CouponPresenter;
import xiaoe.com.shop.business.coupon.ui.CouponFragment;
import xiaoe.com.shop.interfaces.OnSelectCouponListener;
import xiaoe.com.shop.widget.StatusPagerView;

public class PayActivity extends XiaoeActivity implements View.OnClickListener, OnSelectCouponListener {
    private static final String TAG = "PayActivity";
    private final int LOADING = 10001;
    private final int FINISH = 10002;
    private final int FAIL = 10003;
    private final int EMPTY = 10004;
    private final int PAY_SUCCEED = 10005;
    private FragmentManager manager;
    private FragmentTransaction fragmentTransaction;
    private ArrayList<BaseFragment> fragments = null;
    private Intent mIntent;
    private StatusPagerView statusPagerView;
    private ImageView payBack;
    private TextView payTitle;
    private boolean selectCouponPager = false;
    private CouponPresenter mCouponPresenter;
    private CouponFragment couponFragment;
    private boolean initCoupon = false;
    private boolean isAdd = false;
    private int payPrice;
    private String resourceId;
    private PayingFragment payingFragment;
    private List<CouponInfo> validCoupon;
    private CouponInfo useCouponInfo;
    private int resourceType;
    private boolean paying = false;
    private boolean paySucceed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
    }

    private void initView() {
        mIntent = getIntent();
        payPrice = mIntent.getIntExtra("price",0);
        resourceId = mIntent.getStringExtra("resourceId");
        resourceType = mIntent.getIntExtra("resourceType", 0);
        mCouponPresenter = new CouponPresenter(this);

        validCoupon = new ArrayList<CouponInfo>();

        manager = getSupportFragmentManager();
        fragments = new ArrayList<BaseFragment>();

        payBack = (ImageView) findViewById(R.id.pay_back);
        payBack.setOnClickListener(this);
        payTitle = (TextView) findViewById(R.id.pay_title);

        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        statusPagerView.setVisibility(View.GONE);
        statusPagerView.setLoadingState(View.VISIBLE);
        statusPagerView.setBtnGoToOnClickListener(this);

        showFragment(PayingFragment.class);
        mCouponPresenter.requestResourceUseCoupon(resourceId ,payPrice);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(paying){
            paying = false;
            int code = getWXPayCode(true);
            if(code == 0){
                getDialog().dismissDialog();
                setPagerState(PAY_SUCCEED);
                hideFragment(payingFragment);
            }else {
                SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, -100);
            }
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        if(success){
            if(iRequest instanceof ResourceUseCouponRequest){
                JSONObject jsonObject = (JSONObject) entity;
                resourceUseCouponRequest(jsonObject);
            }else if(iRequest instanceof PayOrderRequest){
                JSONObject data = (JSONObject) entity;
                payOrderRequest(data);
            }
        }else{
            if(iRequest instanceof PayOrderRequest){
                getDialog().dismissDialog();
                getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
                getDialog().showDialog(-1);
            }

        }
    }

    /**
     * 根据.class展示Fragment
     * @param theClass
     */
    public void showFragment(Class theClass){
        fragmentTransaction = manager.beginTransaction();
        boolean has = false;
        BaseFragment showFragment = null;
        for (int i = 0; i < fragments.size(); i++){
            BaseFragment iFragment = fragments.get(i);
            if(iFragment.getClass() == theClass){//要展示的碎片
                has = true;
                showFragment = iFragment;
            }else{//其他需要隐藏的碎片
                if(iFragment.isAdded()){
                    fragmentTransaction.hide(iFragment);
                }
            }
        }

        if(!has){//还未初始化
            BaseFragment baseFragment = createMainFragment(theClass);
            fragments.add(baseFragment);
            fragmentTransaction.add(R.id.frame_layout, baseFragment, "Fragments");
            fragmentTransaction.show(baseFragment);
        }else {
            if(showFragment.isAdded()){//已经添加
                fragmentTransaction.show(showFragment);
            }else{//未添加
                fragmentTransaction.add(R.id.frame_layout, showFragment, "Fragments");
                fragmentTransaction.show(showFragment);
            }
        }
        fragmentTransaction.commit();
    }
    private void hideFragment(BaseFragment fragment){
        fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    private BaseFragment createMainFragment(Class theClass){
        if(theClass == PayingFragment.class){
            payingFragment = new PayingFragment();
            payingFragment.setBtnSucceedPayClickListener(this);
            return payingFragment;
        }else if(theClass == CouponFragment.class){
            couponFragment = CouponFragment.newInstance(R.layout.fragment_coupone);
            couponFragment.setSelectIcon(View.VISIBLE);
            couponFragment.setOnSelectCouponListener(this);
            return couponFragment;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if(selectCouponPager){
            clickBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coupon_wrap:
                clickUseCoupon();
                break;
            case R.id.pay_back:
                clickBack();
                break;
            case R.id.btn_succeed_pay:
                buyResource();
                break;
            case R.id.btn_go_to:
                finish();
                break;
            default:
                break;
        }
    }

    private void buyResource() {
        if(paying){
            return;
        }
        paying = true;
        getDialog().showLoadDialog(false);
        int paymentType = (resourceType == 8 || resourceType == 6) ? 3 : 2;
        if(useCouponInfo != null){
            payOrder(resourceId, resourceType, paymentType, useCouponInfo.getCu_id());
        }else{
            payOrder(resourceId, resourceType, paymentType, null);
        }
    }

    private void payOrderRequest(JSONObject object) {
        if(object.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            getDialog().dismissDialog();
            getDialog().setHintMessage(getResources().getString(R.string.pay_info_error));
            getDialog().showDialog(-1);
            return;
        }
        JSONObject dataObject = object.getJSONObject("data");
        String payState = dataObject.getString("pay_state");
        if("SUCCESS".equals(payState)){
            SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_WX_PLAY_CODE, 0);
            getDialog().dismissDialog();
            setPagerState(PAY_SUCCEED);
            hideFragment(payingFragment);
        }else{
            JSONObject payConfig = dataObject.getJSONObject("payConfig");
            pullWXPay(payConfig.getString("appid"), payConfig.getString("partnerid"), payConfig.getString("prepayid"),
                    payConfig.getString("noncestr"), payConfig.getString("timestamp"), payConfig.getString("package"), payConfig.getString("sign"));
        }

    }

    private void clickUseCoupon() {
        if(validCoupon.size() <= 0){
            return;
        }
        selectCouponPager = true;
        showFragment(CouponFragment.class);
        payTitle.setText(getResources().getString(R.string.coupon_title));
        if(!initCoupon){
            statusPagerView.setVisibility(View.VISIBLE);
        }else if(!isAdd){
            isAdd = true;
            couponFragment.addData(validCoupon);
        }
    }

    private void clickBack() {
        if(selectCouponPager){
            payTitle.setText(getResources().getString(R.string.paying));
            statusPagerView.setVisibility(View.GONE);
            selectCouponPager = false;
            showFragment(PayingFragment.class);
            return;
        }
        finish();
    }


    private void resourceUseCouponRequest(JSONObject jsonObject) {
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
            return;
        }
        initCoupon = true;
        List<CouponInfo> tempValidCoupon = jsonObject.getJSONArray("data").toJavaList(CouponInfo.class);
        if(tempValidCoupon.size() <= 0){
            return;
        }
        for (CouponInfo couponInfo : tempValidCoupon) {
            couponInfo.setValid(true);
            validCoupon.add(couponInfo);
        }
        setPagerState(FINISH);
        if(couponFragment != null){
            isAdd = true;
            couponFragment.addData(validCoupon);
        }

        payingFragment.setCanUseCouponCount(validCoupon.size());
        payingFragment.setBtnSelectCouponClickListener(this);

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
            statusPagerView.setHintStateVisibility(View.VISIBLE);
            statusPagerView.setStateImage(R.mipmap.network_none);
            statusPagerView.setStateText(getResources().getString(R.string.request_fail));
        }else if (state == EMPTY){
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.VISIBLE);
            statusPagerView.setStateImage(R.mipmap.network_none);
            statusPagerView.setStateText(getResources().getString(R.string.coupon_empty_desc));
        }else if(state == PAY_SUCCEED){
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setHintStateVisibility(View.VISIBLE);
            statusPagerView.setStateImage(R.mipmap.payment_success);
            statusPagerView.setStateText(getResources().getString(R.string.pay_succeed));
            statusPagerView.setBtnGoToText(getResources().getString(R.string.go_to_study));
        }
    }

    @Override
    public void onSelect(CouponInfo couponInfo, int position) {
        int index = -1;
        for (CouponInfo ci : couponFragment.getData()) {
            index++;
            if(ci.isSelect()){
                ci.setSelect(false);
                break;
            }
        }
        useCouponInfo = couponInfo;
        couponFragment.notifyItemChanged(index);
        if(couponInfo != null){
            couponFragment.setUseCoupon(true);
            couponInfo.setSelect(true);
            couponFragment.notifyItemChanged(position);
            payingFragment.setUseConponPrice(couponInfo.getPrice());
        }else{
            couponFragment.setUseCoupon(false);
            payingFragment.setUseConponPrice(-1);
        }
    }
}

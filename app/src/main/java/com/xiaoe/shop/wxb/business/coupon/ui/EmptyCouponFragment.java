package com.xiaoe.shop.wxb.business.coupon.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;

public class EmptyCouponFragment extends BaseFragment {

    private static final String TAG = "CouponFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    private int noUseCouponVisibility = View.GONE;

    public static EmptyCouponFragment newInstance(int layoutId) {
        EmptyCouponFragment couponFragment = new EmptyCouponFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        couponFragment.setArguments(bundle);
        return couponFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        return viewWrap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_coupone_empty:
                RelativeLayout noUseCoupon = (RelativeLayout) viewWrap.findViewById(R.id.no_use_coupon);
                noUseCoupon.setVisibility(noUseCouponVisibility);
                break;
            default:
                break;
        }
    }

    public void setNoUseCouponVisibility(int noUseCouponVisibility) {
        this.noUseCouponVisibility = noUseCouponVisibility;
    }
}

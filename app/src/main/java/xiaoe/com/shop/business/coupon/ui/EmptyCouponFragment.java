package xiaoe.com.shop.business.coupon.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.base.BaseFragment;

public class EmptyCouponFragment extends BaseFragment {

    private static final String TAG = "CouponFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    CouponActivity couponActivity;

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
        couponActivity = (CouponActivity) getActivity();
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

        }
    }
}

package com.xiaoe.shop.wxb.business.coupon.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.coupon.CouponListAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.interfaces.OnSelectCouponListener;

public class CouponFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CouponFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    private RecyclerView couponRecyclerView;
    private CouponListAdapter couponListAdapter;
    private int mSelectIconVisibility = View.GONE;
    private OnSelectCouponListener selectCouponListener;
    private int marginTop = 0;
    private RelativeLayout btnNoUseCoupon;
    private boolean useCoupon;
    private ImageView noUseCouponIcon;
    private List<CouponInfo> couponList = new ArrayList<>();
    private boolean isAdd = false;

    public static CouponFragment newInstance(int layoutId) {
        CouponFragment couponFragment = new CouponFragment();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    public void setSelectIcon(int visibility){
        mSelectIconVisibility = visibility;
    }

    public void setMarginTop(int top){
        marginTop = top;
    }

    private void initView() {
        couponRecyclerView = (RecyclerView) viewWrap.findViewById(R.id.coupon_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setAutoMeasureEnabled(true);
        couponRecyclerView.setLayoutManager(layoutManager);
        couponRecyclerView.setItemAnimator(null);
        couponListAdapter = new CouponListAdapter(mContext, mSelectIconVisibility, marginTop);
        couponListAdapter.setOnSelectCouponListener(selectCouponListener);
        couponRecyclerView.setAdapter(couponListAdapter);
        if(!isAdd && couponList != null){
            couponListAdapter.addAll(couponList);
        }

        btnNoUseCoupon = (RelativeLayout) viewWrap.findViewById(R.id.no_use_coupon);
        btnNoUseCoupon.setOnClickListener(this);
        btnNoUseCoupon.setVisibility(mSelectIconVisibility);
        noUseCouponIcon = (ImageView) viewWrap.findViewById(R.id.select_icon);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void addData(List<CouponInfo> list){
        if (couponList != null) {
            couponList.clear();
        }
        couponList.addAll(list);
        if(couponListAdapter == null){
            return;
        }
        isAdd = true;
        couponListAdapter.addAll(list);
    }

    public List<CouponInfo> getData(){
        return couponListAdapter.getCouponList();
    }

    public void notifyItemChanged(int position){
        couponListAdapter.notifyItemChanged(position);
    }

    public void setOnSelectCouponListener(OnSelectCouponListener listener){
        selectCouponListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.no_use_coupon && selectCouponListener != null){
            selectCouponListener.onSelect(null, -1);
            setUseCoupon(useCoupon);
        }
    }
    public void setUseCoupon(boolean use){
        useCoupon = use;
        if(useCoupon){
            noUseCouponIcon.setImageResource(R.mipmap.download_tocheck);
        }else{
            noUseCouponIcon.setImageResource(R.mipmap.download_checking);
        }
    }
}

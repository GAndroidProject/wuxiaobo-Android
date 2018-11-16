package com.xiaoe.shop.wxb.adapter.coupon;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.OnSelectCouponListener;
import com.xiaoe.shop.wxb.widget.CouponView;

public class CouponListHolder extends BaseViewHolder {
    private static final String TAG = "CouponListHolder";
    private final View rootView;
    private final RelativeLayout couponItem;
    private final CouponView couponView;
    private OnSelectCouponListener selectCouponListener;

    public CouponListHolder(View itemView, int showSeleceIcon) {
        super(itemView);
        rootView = itemView;
        couponItem = (RelativeLayout) rootView.findViewById(R.id.coupon_item);
        couponView = (CouponView) rootView.findViewById(R.id.coupon_info);
        couponView.showSelectIcon(showSeleceIcon);
    }
    public void bindView(final CouponInfo couponInfo, boolean existMarginTop, final int position, int marginTop, int marginBottom){
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) couponItem.getLayoutParams();
        if(position == 0 && existMarginTop){
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
        }else{
            layoutParams.setMargins(0, 0, 0, marginBottom);
        }
        couponView.setCouponInfo(couponInfo);
        couponView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectCouponListener != null){
                    selectCouponListener.onSelect(couponInfo, position);
                }
            }
        });
    }

    public void setOnSelectCouponListener(OnSelectCouponListener listener) {
        selectCouponListener = listener;
    }
}

package com.xiaoe.shop.wxb.common.pay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.widget.autotextview.AutofitTextView;

public class PayingFragment extends BaseFragment {
    private static final String TAG = "PayingFragment";
    private View mRootView;
    private Intent mIntent;
    private TextView payPriceView;
    private RelativeLayout btnSelectCoupon;
    private TextView useConpon;
    private int resPrice;
    private TextView btnSucceedPay;
    private View.OnClickListener onClickListener;
    private int couponCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_paying, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIntent = getActivity().getIntent();
        initView();
        initListener();
    }

    private void initView() {
        SimpleDraweeView itemImage = (SimpleDraweeView) mRootView.findViewById(R.id.item_image);
        itemImage.setImageURI(mIntent.getStringExtra("image_url"));
        TextView title = (TextView) mRootView.findViewById(R.id.item_title);
        title.setText(mIntent.getStringExtra("title"));
        TextView resourcePrice = (TextView) mRootView.findViewById(R.id.item_count);
        resPrice = mIntent.getIntExtra("price",0);
        resourcePrice.setText("¥"+(resPrice / 100f));
        payPriceView = (TextView) mRootView.findViewById(R.id.pay_price);
        payPriceView.setText("¥"+(resPrice / 100f));
        btnSelectCoupon = (RelativeLayout) mRootView.findViewById(R.id.coupon_wrap);
        useConpon = (TextView) mRootView.findViewById(R.id.use_coupon_title);
        btnSucceedPay = (TextView) mRootView.findViewById(R.id.btn_succeed_pay);
        setCanUseCouponCount(-1);

        AutofitTextView itemDesc = (AutofitTextView) mRootView.findViewById(R.id.item_desc);
        String expireTime = mIntent.getStringExtra("expireTime");
        if(!TextUtils.isEmpty(expireTime)){
            itemDesc.setVisibility(View.VISIBLE);
            itemDesc.setText(expireTime);
//            itemDesc.setText("有效期至："+expireTime);
        }
    }
    private void initListener() {
        if(onClickListener == null){
            return;
        }
        btnSucceedPay.setOnClickListener(onClickListener);
        btnSelectCoupon.setOnClickListener(onClickListener);
    }

    public void setCanUseCouponCount(int count){
        couponCount = count;
        if(useConpon == null || count <= 0){
            return;
        }
        useConpon.setText("有"+count+"张可用优惠券");
        useConpon.setTextColor(getResources().getColor(R.color.high_title_color));
    }

    public void setBtnSelectCouponClickListener(View.OnClickListener listener){
        if(onClickListener == null){
            onClickListener = listener;
        }
        if(btnSelectCoupon == null){
            return;
        }
        btnSelectCoupon.setOnClickListener(listener);
    }

    public void setBtnSucceedPayClickListener(View.OnClickListener listener){
        if(onClickListener == null){
            onClickListener = listener;
        }
        if(btnSucceedPay == null){
            return;
        }
        btnSucceedPay.setOnClickListener(listener);
    }

    public void setUseConponPrice(int price){
        if(price <= 0){
            price = 0;
            useConpon.setText(getResources().getString(R.string.not_can_use_or_no_sue_coupon));
            useConpon.setTextColor(getResources().getColor(R.color.secondary_title_color));
        }else{
            useConpon.setText("-¥" + (price / 100f));
            useConpon.setTextColor(getResources().getColor(R.color.high_title_color));
        }
        int showPrice = resPrice - price;
        if(showPrice < 0){
            showPrice = 0;
        }
        payPriceView.setText("¥"+(showPrice / 100f));

    }
}

package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;

public class CommonBuyView extends LinearLayout {

    private TextView vipBtn;
    private TextView buyBtn;

    public CommonBuyView(Context context) {
        super(context);
        init(context);
    }

    public CommonBuyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonBuyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CommonBuyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.common_buy, this);
        vipBtn = (TextView) view.findViewById(R.id.buy_vip);
        buyBtn = (TextView) view.findViewById(R.id.buy_course);
    }

    public void setBuyPrice(int price){
        float priceF = price / (100*1.0f);
        buyBtn.setText("购买¥"+priceF);
    }

    public void setOnVipBtnClickListener(OnClickListener listener) {
        vipBtn.setOnClickListener(listener);
    }

    public void setOnBuyBtnClickListener(OnClickListener listener) {
        buyBtn.setOnClickListener(listener);
    }

    public void setBuyBtnText(String price) {
        buyBtn.setText(price);
    }

    public void setVipBtnVisibility(int visibility) {
        vipBtn.setVisibility(visibility);
    }
}

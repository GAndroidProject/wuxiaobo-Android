package com.xiaoe.shop.wxb.business.mine.presenter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class MoneyItemViewHolder extends BaseViewHolder {

    @BindView(R.id.money_item_wrap)
    LinearLayout money_item_wrap;
    @BindView(R.id.money_item_title)
    TextView money_item_title;
    @BindView(R.id.money_item_title2)
    TextView money_item_title2;
    @BindView(R.id.money_item_desc)
    TextView money_item_desc;

    MoneyItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

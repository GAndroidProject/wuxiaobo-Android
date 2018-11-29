package com.xiaoe.shop.wxb.business.mine.presenter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LearningListItemViewHolder{

    @BindView(R.id.learning_list_item_icon)
    ImageView itemIcon;
    @BindView(R.id.learning_list_item_title)
    TextView itemTitle;
    @BindView(R.id.learning_list_item_coupon)
    LinearLayout itemCouponContainer;
    @BindView(R.id.tip)
    TextView itemTip;
    @BindView(R.id.content)
    TextView itemContent;

    LearningListItemViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

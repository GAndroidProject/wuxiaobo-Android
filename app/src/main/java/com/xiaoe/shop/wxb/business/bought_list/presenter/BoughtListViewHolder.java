package com.xiaoe.shop.wxb.business.bought_list.presenter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;

public class BoughtListViewHolder {

    @BindView(R.id.bought_list_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.bought_list_item_title)
    TextView itemContent;
    @BindView(R.id.bought_list_share)
    ImageView itemShare;

    public BoughtListViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

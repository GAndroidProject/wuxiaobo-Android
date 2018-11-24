package com.xiaoe.shop.wxb.adapter.decorate.recent_update;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.shop.wxb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

// 最近更新 list item 的 viewHolder
public class RecentUpdateListViewHolder {

    @BindView(R.id.recent_update_item_wrap)
    RelativeLayout itemWrap;
    @BindView(R.id.recent_update_item_title)
    TextView itemTitle;
    @BindView(R.id.recent_update_item_icon)
    SimpleDraweeView itemIcon;

    RecentUpdateListViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

class KnowledgeItemViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_group_item_wrap)
    FrameLayout itemWrap;
    @BindView(R.id.knowledge_group_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.knowledge_group_item_icon_bg)
    SimpleDraweeView itemIconBg;
    @BindView(R.id.knowledge_group_item_title)
    TextView itemTitle;
    @BindView(R.id.knowledge_group_item_title_column)
    TextView itemTitleColumn;
    @BindView(R.id.knowledge_group_item_price)
    TextView itemPrice;
    @BindView(R.id.knowledge_group_item_desc)
    TextView itemDesc;

    KnowledgeItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

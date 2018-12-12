package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;

// 商品分组 ListView 每个 item 的 ViewHolder
class KnowledgeHolder {

    @BindView(R.id.knowledge_list_item_wrap)
    RelativeLayout itemWrap;
    @BindView(R.id.knowledge_list_item)
    RelativeLayout listItem;
    @BindView(R.id.knowledge_list_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.knowledge_list_item_icon2)
    SimpleDraweeView itemIcon2;
    @BindView(R.id.knowledge_list_item_icon_bg)
    SimpleDraweeView itemIconBg;
    @BindView(R.id.knowledge_list_item_title)
    TextView itemTitle;
    @BindView(R.id.knowledge_list_item_title_column)
    TextView itemTitleColumn;
    @BindView(R.id.knowledge_list_item_price)
    TextView itemPrice;
    @BindView(R.id.knowledge_list_item_desc)
    TextView itemDesc;

    KnowledgeHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

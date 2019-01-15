package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.shop.wxb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 知识商品，列表形式
 *
 * @author: zak
 * @date: 2019/1/14
 */
public class NewKnowledgeHolder {

    @BindView(R.id.knowledge_new_list_item_wrap)
    RelativeLayout itemWrap;
    @BindView(R.id.knowledge_new_list_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.knowledge_new_list_item_title)
    TextView itemTitle;
    @BindView(R.id.knowledge_new_list_item_desc)
    TextView itemDesc;
    @BindView(R.id.knowledge_new_list_item_learn)
    TextView itemLearn; // 学习次数
    @BindView(R.id.knowledge_new_list_item_price)
    TextView itemPrice;

    NewKnowledgeHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}

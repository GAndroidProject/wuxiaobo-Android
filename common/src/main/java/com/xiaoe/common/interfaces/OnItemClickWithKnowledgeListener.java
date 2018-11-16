package com.xiaoe.common.interfaces;

import android.view.View;

import com.xiaoe.common.entitys.KnowledgeCommodityItem;

public interface OnItemClickWithKnowledgeListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param knowledgeCommodityItem 点击的 view 对象的知识商品组件对象
     */
    public void onKnowledgeItemClick(View view, KnowledgeCommodityItem knowledgeCommodityItem);
}

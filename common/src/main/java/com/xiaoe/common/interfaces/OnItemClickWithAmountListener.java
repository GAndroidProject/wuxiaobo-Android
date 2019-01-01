package com.xiaoe.common.interfaces;

import android.view.View;

import com.xiaoe.common.entitys.AmountItemEntity;

/**
 * @author: zak
 * @date: 2018/12/29
 */
public interface OnItemClickWithAmountListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param amountItemEntity 点击的 view 对象的金额 item
     */
    public void onAmountItemClick(View view, AmountItemEntity amountItemEntity, int position);
}

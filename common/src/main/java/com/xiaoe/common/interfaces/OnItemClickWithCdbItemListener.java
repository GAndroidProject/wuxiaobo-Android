package com.xiaoe.common.interfaces;

import android.view.View;

import com.xiaoe.common.entitys.CommonDownloadBean;

/**
 * @author: zak
 * @date: 2019/1/4
 */
public interface OnItemClickWithCdbItemListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param commonDownloadBean 点击的 view 对象的知识商品组件对象
     */
    public void onCommonDownloadBeanItemClick(View view, CommonDownloadBean commonDownloadBean);
}
